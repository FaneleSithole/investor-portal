const API_BASE = '/api';
let isSignup = false;

function showToast(msg, success = true) {
  const t = document.getElementById('toast');
  if (!t) return;
  t.textContent = msg;
  t.style.background = success ? '#1a6b3c' : '#dc2626';
  t.style.opacity = '1';
  setTimeout(() => { t.style.opacity = '0'; }, 3800);
}

function showAuthError(msg) {
  const el = document.getElementById('auth-error');
  el.textContent = msg || '';
  el.style.display = msg ? 'block' : 'none';
}

async function parseError(r) {
  try {
    const body = await r.json();
    const message = body.message || `Request failed (${r.status})`;
    const parsed = parseServerValidationMessage(message);
    const err = new Error(parsed.message);
    err.fieldErrors = parsed.fieldErrors;
    return err;
  } catch {
    return new Error(`Request failed (${r.status})`);
  }
}

function setMode(signup) {
  isSignup = signup;
  document.getElementById('auth-title').textContent = signup ? 'Create Account' : 'Institutional Login';
  document.getElementById('auth-submit').textContent = signup ? 'Create Account' : 'Access Portal';
  document.getElementById('signup-fields').classList.toggle('visible', signup);
  document.getElementById('forgot-row').classList.toggle('hidden', signup);
  document.getElementById('toggle-mode').textContent = signup ? 'Login' : 'Sign Up';
  document.getElementById('auth-switch').innerHTML = signup
    ? 'Already have an account? <a href="#" id="toggle-mode">Login</a>'
    : 'New to Capital Flow? <a href="#" id="toggle-mode">Sign Up</a>';
  document.getElementById('toggle-mode').addEventListener('click', toggleMode);
  clearFieldErrors(document.getElementById('auth-form'));
  showAuthError('');
}

function toggleMode(e) {
  e.preventDefault();
  setMode(!isSignup);
}

async function handleSubmit(e) {
  e.preventDefault();
  clearFieldErrors(document.getElementById('auth-form'));
  showAuthError('');

  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  const btn = document.getElementById('auth-submit');

  if (isSignup) {
    const firstName = document.getElementById('first-name').value.trim();
    const lastName = document.getElementById('last-name').value.trim();
    const firmName = document.getElementById('firm-name').value.trim();
    const dateOfBirth = document.getElementById('date-of-birth').value;

    const result = ValidationRules.validateRegister({
      email, password, firstName, lastName, firmName, dateOfBirth
    });
    if (!applyValidationResult('auth', result, 'auth-error')) {
      showToast(result.message, false);
      return;
    }

    btn.disabled = true;
    try {
      const r = await fetch(API_BASE + '/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email, password, firstName, lastName, firmName: firmName || null, dateOfBirth })
      });
      if (!r.ok) throw await parseError(r);
      showToast('Account created — welcome to Capital Flow');
      setTimeout(() => { window.location.href = 'index.html'; }, 600);
    } catch (err) {
      const msg = err.message || 'Unable to reach the server. Start the backend on port 8080 and open http://localhost:8080/login.html';
      applyApiError('auth', err, 'auth-error');
      showAuthError(msg);
      showToast(msg, false);
      btn.disabled = false;
    }
    return;
  }

  const result = ValidationRules.validateLogin({ email, password });
  if (!applyValidationResult('auth', result, 'auth-error')) {
    showToast(result.message, false);
    return;
  }

  btn.disabled = true;
  try {
    const r = await fetch(API_BASE + '/auth/sessions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ email, password })
    });
    if (!r.ok) throw await parseError(r);
    showToast('Login successful');
    setTimeout(() => { window.location.href = 'index.html'; }, 600);
  } catch (err) {
    const msg = err.message || 'Unable to reach the server. Start the backend on port 8080 and open http://localhost:8080/login.html';
    applyApiError('auth', err, 'auth-error');
    showAuthError(msg);
    showToast(msg, false);
    btn.disabled = false;
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const params = new URLSearchParams(window.location.search);
  setMode(params.get('mode') === 'signup');

  document.getElementById('auth-form').addEventListener('submit', handleSubmit);
  document.getElementById('toggle-mode').addEventListener('click', toggleMode);

  document.getElementById('password-toggle').addEventListener('click', () => {
    const input = document.getElementById('password');
    input.type = input.type === 'password' ? 'text' : 'password';
  });

  wireFieldClearOnInput(document.getElementById('auth-form'), [
    'email', 'password', 'first-name', 'last-name', 'firm-name', 'date-of-birth'
  ]);

  fetch(API_BASE + '/auth/me', { credentials: 'include' })
    .then(r => { if (r.ok) window.location.href = 'index.html'; });
});
