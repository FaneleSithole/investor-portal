const NOTIF_STORAGE_KEY = 'cf_notifications';
const FRESH_LOGIN_KEY = 'cf_fresh_login';
const WELCOME_SIGNUP_KEY = 'cf_welcome_signup';

function displayName(user) {
  if (user.fullName) return user.fullName;
  return [user.firstName, user.lastName].filter(Boolean).join(' ') || 'Investor';
}

function loadNotifications() {
  try {
    const raw = sessionStorage.getItem(NOTIF_STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

function saveNotifications(list) {
  sessionStorage.setItem(NOTIF_STORAGE_KEY, JSON.stringify(list));
}

function getUnreadCount() {
  return loadNotifications().filter(n => !n.read).length;
}

function formatNotifTime(iso) {
  const d = new Date(iso);
  return d.toLocaleString('en-ZA', {
    month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
  });
}

function buildWelcomeNotification(user, isSignup) {
  const name = displayName(user);
  const first = user.firstName || 'Investor';
  if (isSignup) {
    return {
      id: 'welcome-' + Date.now(),
      type: 'welcome',
      title: 'Welcome to Capital Flow',
      message: `Dear ${name}, your institutional investor account has been created and verified. You may now access portfolio management, withdrawal requests, and compliance reporting.`,
      time: new Date().toISOString(),
      read: false
    };
  }
  return {
    id: 'welcome-' + Date.now(),
    type: 'welcome',
    title: 'Secure Session Established',
    message: `Welcome back, ${name}. You have been authenticated to the Enviro365 Capital Flow portal. Your session is active and all portfolio services are available.`,
    time: new Date().toISOString(),
    read: false
  };
}

function buildPortfolioNotification() {
  return {
    id: 'portfolio-' + Date.now(),
    type: 'portfolio',
    title: 'Portfolio Dashboard Ready',
    message: 'Your latest holdings, performance metrics, and asset allocation are available on the dashboard.',
    time: new Date().toISOString(),
    read: false
  };
}

function buildComplianceNotification() {
  return {
    id: 'compliance-' + Date.now(),
    type: 'compliance',
    title: 'Compliance Monitoring Active',
    message: 'Regulatory rule health checks are current. Review any active alerts under Compliance.',
    time: new Date().toISOString(),
    read: false
  };
}

function updateNotifBadge() {
  const badge = document.getElementById('notif-badge');
  if (!badge) return;
  const count = getUnreadCount();
  badge.textContent = count > 99 ? '99+' : String(count);
  badge.hidden = count === 0;
  badge.dataset.count = String(count);
}

function renderNotificationList() {
  const list = document.getElementById('notif-list');
  if (!list) return;

  const notifications = loadNotifications();
  if (!notifications.length) {
    list.innerHTML = '<div class="notif-empty">No notifications</div>';
    return;
  }

  list.innerHTML = notifications.map(n => `
    <div class="notif-item${n.read ? ' read' : ''}" data-id="${n.id}">
      <div class="notif-item-icon notif-icon-${n.type}">${notifIcon(n.type)}</div>
      <div class="notif-item-body">
        <div class="notif-item-title">${n.title}</div>
        <div class="notif-item-msg">${n.message}</div>
        <div class="notif-item-time">${formatNotifTime(n.time)}</div>
      </div>
      ${n.read ? '' : '<span class="notif-unread-dot" aria-hidden="true"></span>'}
    </div>
  `).join('');

  list.querySelectorAll('.notif-item').forEach(el => {
    el.addEventListener('click', () => markNotificationRead(el.dataset.id));
  });
}

function notifIcon(type) {
  const icons = {
    welcome: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
    portfolio: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg>',
    compliance: '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>'
  };
  return icons[type] || icons.welcome;
}

function markNotificationRead(id) {
  const list = loadNotifications();
  const item = list.find(n => n.id === id);
  if (!item || item.read) return;
  item.read = true;
  saveNotifications(list);
  updateNotifBadge();
  renderNotificationList();
}

function markAllNotificationsRead() {
  const list = loadNotifications();
  if (!list.some(n => !n.read)) return;
  list.forEach(n => { n.read = true; });
  saveNotifications(list);
  updateNotifBadge();
  renderNotificationList();
}

function toggleNotifPanel(forceOpen) {
  const panel = document.getElementById('notif-panel');
  const trigger = document.getElementById('notif-trigger');
  if (!panel || !trigger) return;

  const open = forceOpen === true ? true : forceOpen === false ? false : !panel.classList.contains('open');
  panel.classList.toggle('open', open);
  trigger.setAttribute('aria-expanded', open ? 'true' : 'false');
  if (open) renderNotificationList();
}

function setupNotificationUI() {
  const icon = document.querySelector('.topbar-icon[title="Notifications"]');
  if (!icon || document.getElementById('notif-panel')) return;

  icon.classList.add('notif-trigger');
  icon.id = 'notif-trigger';
  icon.setAttribute('role', 'button');
  icon.setAttribute('aria-label', 'Notifications');
  icon.setAttribute('aria-expanded', 'false');
  icon.setAttribute('tabindex', '0');

  const badge = document.createElement('span');
  badge.className = 'notif-badge';
  badge.id = 'notif-badge';
  badge.hidden = true;
  badge.setAttribute('aria-label', 'Unread notifications');
  icon.appendChild(badge);

  const panel = document.createElement('div');
  panel.className = 'notif-panel';
  panel.id = 'notif-panel';
  panel.innerHTML = `
    <div class="notif-panel-header">
      <h3>Notifications</h3>
      <button type="button" class="notif-mark-read" id="notif-mark-read">Mark all read</button>
    </div>
    <div class="notif-list" id="notif-list"></div>
  `;
  document.body.appendChild(panel);

  icon.addEventListener('click', (e) => {
    e.stopPropagation();
    toggleNotifPanel();
  });
  icon.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      toggleNotifPanel();
    }
  });
  document.getElementById('notif-mark-read').addEventListener('click', markAllNotificationsRead);
  document.addEventListener('click', (e) => {
    if (!panel.contains(e.target) && !icon.contains(e.target)) {
      toggleNotifPanel(false);
    }
  });
}

function showFormalWelcomeToast(user, isSignup) {
  const first = user.firstName || 'Investor';
  const msg = isSignup
    ? `Welcome to Capital Flow, ${first}. Your institutional account is now active.`
    : `Welcome back, ${first}. Your secure session has been established.`;
  if (typeof showToast === 'function') showToast(msg, true);
}

function handleFreshLogin(user) {
  if (sessionStorage.getItem(FRESH_LOGIN_KEY) !== '1') return;

  sessionStorage.removeItem(FRESH_LOGIN_KEY);
  const isSignup = sessionStorage.getItem(WELCOME_SIGNUP_KEY) === '1';
  sessionStorage.removeItem(WELCOME_SIGNUP_KEY);

  const incoming = [buildWelcomeNotification(user, isSignup)];
  if (!isSignup) {
    incoming.push(buildPortfolioNotification(), buildComplianceNotification());
  }

  saveNotifications(incoming);
  updateNotifBadge();
  renderNotificationList();
  showFormalWelcomeToast(user, isSignup);

  setTimeout(() => toggleNotifPanel(true), 400);
}

function initNotifications(user) {
  if (!user || user.error || user.unauthorized) return;
  setupNotificationUI();
  handleFreshLogin(user);
  updateNotifBadge();
  renderNotificationList();
}

function clearNotificationSession() {
  sessionStorage.removeItem(NOTIF_STORAGE_KEY);
  sessionStorage.removeItem(FRESH_LOGIN_KEY);
  sessionStorage.removeItem(WELCOME_SIGNUP_KEY);
}
