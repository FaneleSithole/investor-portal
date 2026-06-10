let selectedFund = null;
let userFullName = '';

// Offline fallback — must stay in sync with backend FundCatalog
const AVAILABLE_FUNDS = [
  { id: 'fund_balanced', name: 'Balanced / Hybrid Funds', horizon: 'Medium-Long Term',
    description: 'Diversified exposure across equities, bonds, and alternatives with moderate volatility.' },
  { id: 'fund_target_date', name: 'Target Date Funds', horizon: 'Medium-Long Term',
    description: 'Age-based allocation that automatically rebalances toward conservative assets over time.' },
  { id: 'fund_etf', name: 'Exchange-Traded Funds (ETFs)', horizon: 'Medium-Long Term',
    description: 'Low-cost, liquid market exposure across sectors, indices, and asset classes.' },
  { id: 'fund_pe', name: 'Private Equity Funds', horizon: 'Long Term',
    description: 'Institutional access to buyout, growth, and venture strategies.' },
  { id: 'fund_mm', name: 'Money Market Funds', horizon: 'Short Term',
    description: 'Short-duration, high-liquidity instruments for capital preservation.' },
  { id: 'fund_reit', name: 'Real Estate Investment Trusts (REITs)', horizon: 'Medium-Long Term',
    description: 'Income-focused commercial and residential property portfolios.' },
  { id: 'fund_esg', name: 'Sustainable & ESG Funds', horizon: 'Medium-Long Term',
    description: 'Impact-aligned strategies integrating environmental and governance criteria.' }
];

function setFundingDateMin() {
  const dateInput = document.getElementById('inv-date');
  if (!dateInput) return;
  const today = new Date();
  dateInput.min = today.toISOString().slice(0, 10);
}

async function loadInvestmentPage() {
  setFundingDateMin();

  const [funds, accounts, profile] = await Promise.all([
    apiFetch('/investments/funds'),
    apiFetch('/accounts/linked'),
    apiFetch('/auth/me')
  ]);

  if (funds?.unauthorized || accounts?.unauthorized) {
    window.location.href = 'login.html';
    return;
  }

  if (profile && !profile.error) {
    userFullName = `${profile.firstName} ${profile.lastName}`;
    document.getElementById('signature-name').placeholder = userFullName;
  }

  if (accounts && !accounts.error) {
    const sel = document.getElementById('inv-account');
    sel.innerHTML = '<option value="">Select Bank Account</option>' +
      accounts.map(a => `<option value="${a.id}">${a.bankName} ****${a.lastFour}</option>`).join('');
  }

  const fundList = (funds && !funds.error && funds.length) ? funds : AVAILABLE_FUNDS;
  if (funds?.error) showToast('Could not load funds from server — showing catalogue', false);
  renderFunds(fundList);

  const amountInput = document.getElementById('inv-amount');
  if (amountInput) {
    amountInput.min = ValidationRules.MIN_COMMITMENT;
    amountInput.step = '0.01';
  }
}

function renderFunds(funds) {
  const grid = document.getElementById('fund-grid');
  const cards = (funds || []).map(f => `
    <button type="button" class="fund-card" data-fund-id="${f.id}" onclick="selectFund('${f.id}', this)">
      <div class="fund-card-tag">${f.horizon}</div>
      <div class="fund-card-title">${f.name}</div>
      <div class="fund-card-desc">${f.description}</div>
    </button>`).join('');

  grid.innerHTML = cards + `
    <button type="button" class="fund-card fund-card-explore" onclick="showToast('Additional funds catalogue coming soon', false)">
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M2 12h20M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></svg>
      <div class="fund-card-title">Explore more funds</div>
      <div class="fund-card-desc">Browse the full institutional catalogue</div>
    </button>`;
}

async function selectFund(fundId, el) {
  document.querySelectorAll('.fund-card:not(.fund-card-explore)').forEach(c => c.classList.remove('selected'));
  el.classList.add('selected');
  setFundSelectionError('');

  const fund = await apiFetch(`/investments/funds/${fundId}`);
  selectedFund = fund?.error
    ? AVAILABLE_FUNDS.find(f => f.id === fundId)
    : fund;

  if (!selectedFund) {
    setFundSelectionError('Fund not found');
    showToast('Fund not found', false);
    return;
  }
  document.getElementById('commitment-amount-display').textContent = fmtFull(0);
  document.getElementById('fund-detail-hint').textContent = selectedFund.name;
  document.getElementById('commitment-hint').textContent =
    `Min. commitment: ${fmt(ValidationRules.MIN_COMMITMENT)} · ${selectedFund.horizon}`;
}

function updateCommitmentDisplay() {
  const amount = parseFloat(document.getElementById('inv-amount').value) || 0;
  document.getElementById('commitment-amount-display').textContent = fmtFull(amount);
}

async function submitInvestment() {
  clearFieldErrors(document.querySelector('.investment-content'));
  showFormError('inv-error', '');

  const amount = parseFloat(document.getElementById('inv-amount').value);
  const accountId = document.getElementById('inv-account').value;
  const fundingDate = document.getElementById('inv-date').value;
  const accredited = document.getElementById('chk-accredited').checked;
  const terms = document.getElementById('chk-terms').checked;
  const signature = document.getElementById('signature-name').value.trim();
  const btn = document.getElementById('btn-submit-inv');

  const result = ValidationRules.validateInvestment({
    fundId: selectedFund?.id,
    amount,
    accountId,
    fundingDate,
    accreditedInvestor: accredited,
    termsAccepted: terms,
    digitalSignature: signature,
    userFullName
  });

  if (!applyValidationResult('investment', result, 'inv-error')) {
    showToast(result.message, false);
    return;
  }

  btn.disabled = true;
  btn.textContent = 'Submitting…';

  try {
    const payload = await apiPost('/investments/commitments', {
      fundId: selectedFund.id,
      amount,
      accountId,
      fundingDate,
      accreditedInvestor: accredited,
      termsAccepted: terms,
      digitalSignature: signature
    });
    showToast(payload.message + ' (Ref: ' + payload.referenceId + ')');
    setTimeout(() => { window.location.href = 'index.html'; }, 1200);
  } catch (e) {
    applyApiError('investment', e, 'inv-error');
    showToast(e.message, false);
    btn.disabled = false;
    btn.innerHTML = 'Review & Submit <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadInvestmentPage();
  document.getElementById('inv-amount')?.addEventListener('input', updateCommitmentDisplay);

  wireFieldClearOnInput(document.querySelector('.investment-content'), [
    'inv-amount', 'inv-account', 'inv-date', 'chk-accredited', 'chk-terms', 'signature-name'
  ]);
});
