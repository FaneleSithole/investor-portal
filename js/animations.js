(function () {
  const FADE_OUT_MS = 820;
  const MAX_STAGGER_DELAY = 1020;

  const PAGE_ANIMATION_SELECTORS = [
    '.sidebar-brand > .brand-logo',
    '.sidebar-brand > .btn-new-investment',
    '.sidebar-nav .nav-item',
    '.sidebar-footer .nav-item',
    '.topbar > .topbar-icon',
    '.topbar > .topbar-divider',
    '.topbar-profile',
    '.page-content > *',
    '.investment-content > *',
    '.profile-content > .profile-hero-card',
    '.profile-content > .profile-grid > *',
    '.auth-layout > section',
    '.auth-card > *',
    '.landing-header > *',
    '.landing-hero > *'
  ].join(', ');

  function fadeIn(el, delay = 0) {
    if (!el) return;
    el.classList.remove('ui-fade-out', 'is-hidden');
    el.classList.add('ui-fade-pending');
    el.style.animationDelay = delay ? `${delay}ms` : '';
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        el.classList.remove('ui-fade-pending');
        el.classList.add('ui-fade-in');
      });
    });
  }

  function fadeOut(el, onDone) {
    if (!el) return;
    el.classList.remove('ui-fade-in');
    el.classList.add('ui-fade-out');

    let done = false;
    const finish = () => {
      if (done) return;
      done = true;
      el.classList.remove('ui-fade-out');
      onDone?.();
    };

    const onEnd = (e) => {
      if (e.target !== el || e.animationName !== 'uiFadeOut') return;
      el.removeEventListener('animationend', onEnd);
      finish();
    };

    el.addEventListener('animationend', onEnd);
    setTimeout(finish, FADE_OUT_MS + 50);
  }

  function animateFadeIn(target, stagger = 45) {
    const list = !target
      ? []
      : target instanceof Element
        ? [target]
        : typeof target === 'string'
          ? [...document.querySelectorAll(target)]
          : [...target];

    list.forEach((el, i) => fadeIn(el, Math.min(i * stagger, MAX_STAGGER_DELAY)));
  }

  function setVisibleWithFade(el, visible, display = 'block') {
    if (!el) return;
    if (visible) {
      el.style.display = display;
      fadeIn(el);
      return;
    }

    if (el.style.display === 'none' && !el.classList.contains('ui-fade-in')) {
      return;
    }

    fadeOut(el, () => {
      el.style.display = 'none';
    });
  }

  function initPageAnimations() {
    const targets = document.querySelectorAll(PAGE_ANIMATION_SELECTORS);
    animateFadeIn(targets, 45);
  }

  window.uiFadeIn = fadeIn;
  window.uiFadeOut = fadeOut;
  window.uiAnimateFadeIn = animateFadeIn;
  window.uiSetVisible = setVisibleWithFade;

  document.addEventListener('DOMContentLoaded', initPageAnimations);
})();
