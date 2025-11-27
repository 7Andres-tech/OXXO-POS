// /resources/static/js/api.js
(function (global) {
  'use strict';

  async function fetchJSON(url, opts={}){
    const headers = Object.assign(
      { 'Content-Type':'application/json' },
      Auth.authHeader(),               // <-- agrega Bearer
      opts.headers||{}
    );
    const r = await fetch(url, { ...opts, headers });
    if (r.status === 401 || r.status === 403) throw new Error('UNAUTHORIZED');
    if (!r.ok) throw new Error(await r.text());
    return r.status === 204 ? null : r.json();
  }

  const API = {
    productos: {
      list: () => fetchJSON('/api/productos'),
      kpis: () => fetchJSON('/api/productos/kpis'),
      toggle: (sku,activo) => fetchJSON(`/api/productos/${encodeURIComponent(sku)}/activo`, {
        method:'PUT', body: JSON.stringify({ activo })
      }),
      del: (sku) => fetchJSON(`/api/productos/${encodeURIComponent(sku)}`, { method:'DELETE' })
    },
    inventario: {
      list: () => fetchJSON('/api/inventario'),
      kpis:  () => fetchJSON('/api/inventario/kpis'),
      ajustar: (payload) => fetchJSON('/api/inventario/ajustar', {
        method:'POST', body: JSON.stringify(payload)
      })
    },

    ventas: {
      async registrar(payload) {
        const r = await fetch('/api/ventas', {
          method: 'POST',
          headers: Auth.authHeaderJson(),  // lo mismo que usas en productos
          body: JSON.stringify(payload)
        });
        if (!r.ok) throw new Error(await r.text());
        return r.json();
      },
      async ultimas() {
        const r = await fetch('/api/ventas/ultimas', { headers: Auth.authHeaderJson() });
        if (!r.ok) throw new Error(await r.text());
        return r.json();
      }
    }
    // … añade otros módulos aquí
  };

  global.API = API;
})(window);
