// /resources/static/js/auth.js
(function (w) {
  'use strict';

  const KEY = 'auth';

  const get = () => { try { return JSON.parse(localStorage.getItem(KEY) || 'null'); } catch { return null; } };
  const set = (a) => localStorage.setItem(KEY, JSON.stringify(a || {}));
  const clear = () => localStorage.removeItem(KEY);

  function normalizeToken(t) {
    if (!t) return null;
    return String(t).replace(/^Bearer\s+/i, '').trim(); // guarda SIN "Bearer "
  }

  async function login(email, password) {
    const r = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    if (!r.ok) throw new Error('Credenciales inválidas');

    const d = await r.json(); // {token, role, name, email}
    const token = normalizeToken(d.token);

    set({
      email: d.email || email,
      name: d.name || email.split('@')[0],
      role: String(d.role || '').toUpperCase(),
      token // ✅ guardado sin Bearer
    });

    return get();
  }

  function authHeader() {
    const t = get()?.token;
    if (!t) return {};
    return { Authorization: `Bearer ${t}` }; // ✅ siempre 1 solo Bearer
  }

  function role() { return String(get()?.role || '').toUpperCase(); }
  function name() { return get()?.name || 'Usuario'; }

  function applyNav() {
    const r = role();
    document.querySelectorAll('[data-roles]').forEach(el => {
      const ok = (el.dataset.roles || '')
        .split(',').map(s => s.trim().toUpperCase())
        .includes(r);
      if (!ok) el.remove();
    });
    const nn = document.getElementById('navUserName');
    if (nn) nn.textContent = name();
  }

  function requireAuth(roles) {
    const a = get();
    if (!a || !a.token) { location.href = 'index.html'; return false; }

    if (roles?.length && !roles.map(s => s.toUpperCase()).includes(role())) {
      location.href = (role() === 'CAJERO') ? 'venta.html' : 'inicio.html';
      return false;
    }
    return true;
  }

  function logout() { clear(); location.href = 'index.html'; }

  w.Auth = { get, set, clear, login, authHeader, role, name, applyNav, requireAuth, logout };
  document.addEventListener('DOMContentLoaded', applyNav);
})(window);
