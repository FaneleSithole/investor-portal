const API_BASE = '/api';
const PUBLIC_PAGES = ['landing.html', 'login.html', ''];

function fmt(n) {
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR', maximumFractionDigits: 0 }).format(n);
}

function fmtFull(n) {
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR', minimumFractionDigits: 2 }).format(n);
}

function fmtDate(str) {
  if (!str) return '—';
  return new Date(str).toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' });
}

async function parseErrorResponse(r) {
  try {
    const body = await r.json();
    const message = body.message || `Request failed (${r.status})`;
    const parsed = typeof parseServerValidationMessage === 'function'
      ? parseServerValidationMessage(message)
      : { message, fieldErrors: {} };
    return { message: parsed.message, fieldErrors: parsed.fieldErrors, status: r.status };
  } catch {
    return { message: `Request failed (${r.status})`, fieldErrors: {}, status: r.status };
  }
}

function createApiError(parsed) {
  const err = new Error(parsed.message);
  err.fieldErrors = parsed.fieldErrors || {};
  err.status = parsed.status;
  return err;
}

async function apiFetch(path) {
  try {
    const r = await fetch(API_BASE + path, {
      headers: { 'Accept': 'application/json' },
      credentials: 'include'
    });
    if (r.status === 401) {
      return { error: 'Authentication required', unauthorized: true };
    }
    if (!r.ok) {
      const parsed = await parseErrorResponse(r);
      console.warn('API error:', path, parsed.message);
      return { error: parsed.message, fieldErrors: parsed.fieldErrors };
    }
    return await r.json();
  } catch (e) {
    console.warn('API error:', path, e);
    return { error: 'Unable to reach the server. Is the backend running on port 8080?' };
  }
}

async function apiPost(path, body) {
  const r = await fetch(API_BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(body)
  });
  if (r.status === 401) throw new Error('Authentication required');
  if (!r.ok) throw createApiError(await parseErrorResponse(r));
  return r.json();
}

async function apiPut(path, body) {
  const r = await fetch(API_BASE + path, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(body)
  });
  if (r.status === 401) throw new Error('Authentication required');
  if (!r.ok) throw createApiError(await parseErrorResponse(r));
  return r.json();
}

async function downloadFile(path, filename) {
  try {
    const r = await fetch(API_BASE + path, { credentials: 'include' });
    if (r.status === 401) {
      window.location.href = 'login.html';
      return false;
    }
    if (!r.ok) throw new Error(await parseErrorResponse(r));
    const blob = await r.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
    return true;
  } catch (e) {
    showToast(e.message || 'Download failed', false);
    return false;
  }
}

function showToast(msg, success = true) {
  const t = document.getElementById('toast');
  if (!t) return;
  t.textContent = msg;
  t.className = 'toast' + (success ? ' success' : '');
  requestAnimationFrame(() => { t.classList.add('show'); });
  setTimeout(() => t.classList.remove('show'), 4200);
}

function showFormError(elId, msg) {
  const el = document.getElementById(elId);
  if (!el) return;
  el.textContent = msg || '';
  el.style.display = msg ? 'block' : 'none';
}

function openNewInvestment() {
  window.location.href = 'investment.html';
}

async function requireAuth() {
  const page = window.location.pathname.split('/').pop() || '';
  if (PUBLIC_PAGES.includes(page)) return;

  const user = await apiFetch('/auth/me');
  if (user?.unauthorized) {
    window.location.href = 'login.html';
    return;
  }
  if (user?.error) {
    showToast(user.error, false);
    return;
  }

  updateTopbarUser(user);
  const firm = document.getElementById('firm-name');
  if (firm && user.firmName) firm.textContent = user.firmName;
}

function updateTopbarUser(user) {
  const initials = (user.firstName?.[0] ?? '') + (user.lastName?.[0] ?? '');
  const avatar = document.getElementById('avatar-initials');
  if (avatar) avatar.textContent = initials || 'U';
  const name = document.getElementById('topbar-name');
  if (name) name.textContent = user.fullName || `${user.firstName} ${user.lastName}`;
  const role = document.getElementById('topbar-role');
  if (role) role.textContent = user.role || 'Institutional Investor';
}

async function logout() {
  await fetch(API_BASE + '/auth/sessions/current', { method: 'DELETE', credentials: 'include' });
  window.location.href = 'landing.html';
}

document.addEventListener('DOMContentLoaded', requireAuth);
