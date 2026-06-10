let balanceData = null;

async function loadWithdrawals() {
  const [summary, history, accounts] = await Promise.all([
    apiFetch('/withdrawals/balance'),
    apiFetch('/withdrawals/transactions'),
    apiFetch('/accounts/linked')
  ]);

  if (summary?.error) {
    showToast(summary.error, false);
    return;
  }

  balanceData = summary;

  if (summary) {
    const parts = fmtFull(summary.availableBalance).split('.');
    document.getElementById('balance-main').textContent = parts[0];
    document.getElementById('balance-cents').textContent = '.' + (parts[1] ?? '00');
    document.getElementById('balance-growth-text').textContent =
      (summary.growthPercent >= 0 ? '+' : '') + summary.growthPercent.toFixed(1) + '% since last quarter';

    const maxEl = document.getElementById('wd-max-hint');
    if (maxEl) {
      maxEl.textContent = 'Maximum withdrawal (90% of balance): ' + fmtFull(summary.maxWithdrawalAmount);
    }

    const amountInput = document.getElementById('wd-amount');
    if (amountInput) {
      amountInput.min = '0.01';
      amountInput.step = '0.01';
    }
  }

  updateRetirementOption(summary);

  if (accounts?.error) {
    showToast(accounts.error, false);
  } else if (accounts) {
    const sel = document.getElementById('wd-account');
    sel.innerHTML = '<option value="">Select account…</option>' +
      accounts.map(a => `<option value="${a.id}">${a.bankName} ****${a.lastFour}</option>`).join('');
  }

  renderHistory(history);
}

function updateRetirementOption(summary) {
  const typeSel = document.getElementById('wd-type');
  const notice = document.getElementById('retirement-notice');
  if (!typeSel || !notice) return;

  const retirementOption = typeSel.querySelector('option[value="RETIREMENT"]');
  const eligible = summary?.retirementEligible === true;

  if (retirementOption) {
    retirementOption.disabled = !eligible;
    if (!eligible && typeSel.value === 'RETIREMENT') {
      typeSel.value = 'STANDARD';
    }
  }

  notice.textContent = eligible
    ? 'You are eligible for retirement withdrawals (age over 65).'
    : 'Retirement withdrawals require age over 65.';
  notice.className = 'form-hint' + (eligible ? ' hint-ok' : ' hint-warn');
}

function renderHistory(history) {
  const tbody = document.getElementById('tx-history-body');

  if (history?.error) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:24px;color:var(--red)">${history.error}</td></tr>`;
    return;
  }

  if (history && history.length) {
    tbody.innerHTML = history.map(tx => `
      <tr>
        <td style="font-family:var(--font-mono);font-size:12px">${fmtDate(tx.date)}</td>
        <td class="tx-amount">${fmtFull(tx.amount)}</td>
        <td style="font-size:12px">${tx.type || 'STANDARD'}</td>
        <td class="tx-dest">${tx.bankName} ****${tx.lastFour}</td>
        <td><span class="status-pill status-${tx.status.toLowerCase()}">${tx.status}</span></td>
      </tr>`).join('');
  } else {
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:24px;color:var(--muted)">No transactions found</td></tr>';
  }
}

async function submitWithdrawal() {
  clearFieldErrors(document.querySelector('.page-content'));
  showFormError('wd-form-error', '');

  const amount = parseFloat(document.getElementById('wd-amount').value);
  const account = document.getElementById('wd-account').value;
  const type = document.getElementById('wd-type').value;
  const reason = document.getElementById('wd-reason').value.trim();
  const btn = document.getElementById('wd-submit-btn');

  const result = ValidationRules.validateWithdrawal(
    { amount, accountId: account, type, reason },
    balanceData
  );
  if (!applyValidationResult('withdrawal', result, 'wd-form-error')) {
    showToast(result.message, false);
    return;
  }

  btn.disabled = true;
  btn.textContent = 'Submitting…';

  try {
    const payload = await apiPost('/withdrawals/request', { amount, accountId: account, type, reason });
    showToast(payload.message + ' (Ref: ' + payload.referenceId + ')');
    document.getElementById('wd-amount').value = '';
    document.getElementById('wd-reason').value = '';
    document.getElementById('wd-account').value = '';
    document.getElementById('wd-type').value = 'STANDARD';
    await loadWithdrawals();
  } catch (e) {
    applyApiError('withdrawal', e, 'wd-form-error');
    showToast(e.message || 'Submission failed', false);
  } finally {
    btn.disabled = false;
    btn.textContent = 'Submit Request';
  }
}

async function exportStatementsCsv() {
  const status = document.getElementById('csv-filter-status')?.value || '';
  const type = document.getElementById('csv-filter-type')?.value || '';
  const from = document.getElementById('csv-filter-from')?.value || '';
  const to = document.getElementById('csv-filter-to')?.value || '';

  const params = new URLSearchParams();
  if (status) params.set('status', status);
  if (type) params.set('type', type);
  if (from) params.set('from', from);
  if (to) params.set('to', to);

  const query = params.toString();
  const path = '/withdrawals/statements/export' + (query ? '?' + query : '');
  const ok = await downloadFile(path, 'withdrawal-statements.csv');
  if (ok) showToast('Statement CSV downloaded');
}

document.addEventListener('DOMContentLoaded', () => {
  loadWithdrawals();
  wireFieldClearOnInput(document.querySelector('.page-content'), [
    'wd-amount', 'wd-account', 'wd-type', 'wd-reason'
  ]);
});
