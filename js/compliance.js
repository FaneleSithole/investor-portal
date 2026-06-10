async function loadCompliance() {
  document.getElementById('last-checked-time').textContent =
    'Last checked: Today, ' + new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

  const data = await apiFetch('/compliance/summary');
  if (!data) return;

  const passRate = data.passRate ?? 0;
  document.getElementById('rule-pass-rate').textContent = passRate + '%';
  document.getElementById('rules-passed').textContent = data.rulesPassed ?? '—';
  document.getElementById('rules-warning').textContent = data.rulesWarning ?? '—';
  document.getElementById('rules-failed').textContent = data.rulesFailed ?? '—';
  document.getElementById('health-bar-fill').style.width = passRate + '%';

  const banner = document.getElementById('breach-banner');
  if (data.activeBreach) {
    banner.style.display = 'block';
    document.getElementById('breach-msg').textContent = data.breachMessage ?? 'A compliance breach has been detected.';
  } else {
    banner.style.display = 'none';
  }
}

document.addEventListener('DOMContentLoaded', loadCompliance);
