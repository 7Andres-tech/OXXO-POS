// /resources/static/js/api.js
(function (global) {
  'use strict';

  function readToken() {
    try {
      const a = JSON.parse(localStorage.getItem('auth') || 'null');
      return a?.token || null; // token sin Bearer (por el auth.js nuevo)
    } catch {
      return null;
    }
  }

  function withAuthHeaders(extra = {}, forceJson = true) {
    const token = readToken();
    const headers = { ...(extra || {}) };

    if (token) headers['Authorization'] = `Bearer ${token}`;
    if (forceJson && !headers['Content-Type']) headers['Content-Type'] = 'application/json';

    return headers;
  }

  async function fetchJSON(url, opts = {}) {
    const isFormData = opts.body instanceof FormData;
    const headers = withAuthHeaders(opts.headers || {}, !isFormData);

    const r = await fetch(url, { ...opts, headers });

    // 401 => token no sirve / no estás logueado
    if (r.status === 401) {
      if (global.Auth?.clear) global.Auth.clear();
      throw new Error('UNAUTHORIZED');
    }

    // 403 => estás logueado pero sin permiso, NO borres token
    if (r.status === 403) {
      const txt = await r.text().catch(() => '');
      throw new Error(txt || 'FORBIDDEN');
    }

    if (!r.ok) throw new Error(await r.text().catch(() => r.statusText));
    if (r.status === 204) return null;

    const ct = r.headers.get('content-type') || '';
    return ct.includes('application/json') ? r.json() : r.text();
  }

  const API = {
    fetchJSON,

    clientes: {
      list: () => fetchJSON('/api/clientes'),
      crear: (payload) => fetchJSON('/api/clientes', { method: 'POST', body: JSON.stringify(payload) }),
      actualizar: (id, payload) => fetchJSON(`/api/clientes/${encodeURIComponent(id)}`, { method: 'PUT', body: JSON.stringify(payload) }),
      del: (id) => fetchJSON(`/api/clientes/${encodeURIComponent(id)}`, { method: 'DELETE' })
    }
  };

  global.API = API;
})(window);
