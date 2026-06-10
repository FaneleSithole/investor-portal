let profileData = null;

function switchTab(tab, btn) {
  document.querySelectorAll('.profile-tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.profile-panel').forEach(p => p.classList.remove('active'));
  if (btn) btn.classList.add('active');
  document.getElementById('panel-' + tab)?.classList.add('active');
}

function markDirty() {
  const btn = document.getElementById('btn-save-profile');
  if (btn) btn.textContent = 'Save Changes *';
}

function populateProfile(p) {
  profileData = p;
  const initials = (p.firstName?.[0] ?? '') + (p.lastName?.[0] ?? '');

  document.getElementById('profile-photo').textContent = initials;
  document.getElementById('profile-name').textContent = p.fullName;
  document.getElementById('profile-subtitle').textContent =
    `${p.role} · ${p.firmName || 'Fanele & Partners'}`;
  document.getElementById('profile-joined').innerHTML =
    `<svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg> Joined ${p.joinedYear}`;

  document.getElementById('field-fullname').value = p.fullName;
  document.getElementById('field-email').value = p.email;
  document.getElementById('field-phone').value = p.phone || '';
  document.getElementById('field-firm').value = p.firmName || '';
  document.getElementById('field-bio').value = p.bio || '';

  document.getElementById('toggle-2fa').checked = p.twoFactorEnabled;
  document.getElementById('notify-portfolio').checked = p.notifyPortfolio;
  document.getElementById('notify-withdrawals').checked = p.notifyWithdrawals;
  document.getElementById('notify-compliance').checked = p.notifyCompliance;
  document.getElementById('notify-reports').checked = p.notifyReports;
  document.getElementById('notify-marketing').checked = p.notifyMarketing;

  const progress = p.securityProgress + '%';
  document.getElementById('security-progress-fill').style.width = progress;
  document.getElementById('security-progress-fill-2').style.width = progress;
}

function enable2FA() {
  document.getElementById('toggle-2fa').checked = true;
  switchTab('security', document.querySelector('[data-tab=security]'));
  markDirty();
  showToast('Enable two-factor authentication and save changes', false);
}

async function saveProfile() {
  const btn = document.getElementById('btn-save-profile');
  const fullName = document.getElementById('field-fullname').value.trim();
  const nameParts = fullName.split(/\s+/).filter(Boolean);
  const firstName = nameParts[0] || '';
  const lastName = nameParts.slice(1).join(' ');
  const phone = document.getElementById('field-phone').value.trim();
  const firmName = document.getElementById('field-firm').value.trim();
  const bio = document.getElementById('field-bio').value.trim();

  clearFieldErrors(document.querySelector('.profile-content'));
  showFormError('profile-form-error', '');

  const result = ValidationRules.validateProfile({ firstName, lastName, phone, firmName, bio });
  if (!applyValidationResult('profile', result, 'profile-form-error')) {
    showToast(result.message, false);
    return;
  }

  const body = {
    firstName: firstName || profileData?.firstName,
    lastName: lastName || profileData?.lastName,
    phone,
    firmName,
    bio,
    twoFactorEnabled: document.getElementById('toggle-2fa').checked,
    notifyPortfolio: document.getElementById('notify-portfolio').checked,
    notifyWithdrawals: document.getElementById('notify-withdrawals').checked,
    notifyCompliance: document.getElementById('notify-compliance').checked,
    notifyReports: document.getElementById('notify-reports').checked,
    notifyMarketing: document.getElementById('notify-marketing').checked
  };

  btn.disabled = true;
  btn.textContent = 'Saving…';

  try {
    const updated = await apiPut('/user/profile', body);
    populateProfile(updated);
    updateTopbarUser(updated);
    showToast('Profile saved successfully');
    btn.textContent = 'Save Changes';
  } catch (e) {
    applyApiError('profile', e, 'profile-form-error');
    showToast(e.message || 'Failed to save profile', false);
    btn.textContent = 'Save Changes *';
  } finally {
    btn.disabled = false;
  }
}

async function loadProfile() {
  const data = await apiFetch('/user/profile/detail');
  if (data?.unauthorized) {
    window.location.href = 'login.html';
    return;
  }
  if (data?.error) {
    showToast(data.error, false);
    return;
  }
  populateProfile(data);
}

document.addEventListener('DOMContentLoaded', () => {
  loadProfile();
  wireFieldClearOnInput(document.querySelector('.profile-content'), [
    'field-fullname', 'field-phone', 'field-firm', 'field-bio'
  ]);
});
