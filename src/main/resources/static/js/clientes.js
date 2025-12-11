// /resources/static/js/clientes.js
(() => {
  "use strict";

  const ventasHist = JSON.parse(
    localStorage.getItem("ventasHist") ||
    localStorage.getItem("vHist") ||
    localStorage.getItem("vhist") ||
    "[]"
  );
  
  const ventasTurno = JSON.parse(
    localStorage.getItem("ventasTurno") ||
    localStorage.getItem("vTurno") ||
    localStorage.getItem("vturno") ||
    "[]"
  );
  

  // ---------- Helpers ----------
  const $ = (s) => document.querySelector(s);
  const PEN = new Intl.NumberFormat("es-PE", { style: "currency", currency: "PEN" });
  const money = (n) => PEN.format(Number(n || 0));

  const modalEl = $("#modalCli");
  const tbody = $("#cliBody");
  const filtroTxt = $("#filtroTxt");
  const filtroOrden = $("#filtroOrden");
  const btnExport = $("#btnExport");

  const kpiClientes = $("#kpiClientes");
  const kpiConCompras = $("#kpiConCompras");
  const kpiPuntosTotales = $("#kpiPuntosTotales");
  const kpiTicketProm = $("#kpiTicketProm");

  const form = $("#formCli");
  const titleCli = $("#titleCli");
  const editDoc = $("#editDoc");
  const cDoc = $("#cDoc");
  const cNom = $("#cNom");
  const cTel = $("#cTel");
  const cMail = $("#cMail");
  const cPts = $("#cPts");

  let CLIENTES = [];

  const docOf = (c) => String(c?.doc ?? "").trim();
  const nomOf = (c) => String(c?.nombre ?? "").trim();
  const telOf = (c) => String(c?.telefono ?? "").trim();
  const mailOf = (c) => String(c?.email ?? "").trim();
  const comprasOf = (c) => Number(c?.compras ?? 0) || 0;
  const puntosOf = (c) => Number(c?.puntos ?? 0) || 0;

  function parseDate(d) {
    if (!d) return null;
    const x = new Date(d);
    return isNaN(x.getTime()) ? null : x;
  }

  function calcularComprasCliente(doc) {
    const lista = [...ventasHist, ...ventasTurno]
      .filter(v => v.clienteDoc === doc);
  
    if (lista.length === 0)
      return { compras: 0, ultima: null };
  
    const ultima = lista.reduce((a, b) =>
      (new Date(a.ts) > new Date(b.ts) ? a : b)
    );
  
    return {
      compras: lista.length,
      ultima: ultima.ts
    };
  }
  window.agregarPuntosCliente = function (doc, puntos) {
    let clientes = JSON.parse(localStorage.getItem('clientes_data') || '[]');

    const c = clientes.find(x => String(x.doc) === String(doc));
    if (c) {
        c.puntos = (c.puntos || 0) + puntos;

        localStorage.setItem('clientes_data', JSON.stringify(clientes));

        const idx = CLIENTES.findIndex(x => String(x.doc) === String(doc));
        if (idx !== -1) CLIENTES[idx].puntos = c.puntos;
    }
};


  

  function getToken() {
    // intenta varias formas (según tu auth.js)
    return (
      window.Auth?.getToken?.() ||
      window.Auth?.token?.() ||
      localStorage.getItem("token") ||
      localStorage.getItem("jwt") ||
      sessionStorage.getItem("token") ||
      ""
    );
  }

  async function readErr(r) {
    const txt = await r.text().catch(() => "");
    if (!txt) return `${r.status} ${r.statusText || "Error"}`;
    try {
      const j = JSON.parse(txt);
      return j.message || j.error || txt;
    } catch {
      return txt;
    }
  }

  async function authFetch(url, opt = {}) {
    const token = getToken();
    const headers = new Headers(opt.headers || {});
    if (token) headers.set("Authorization", `Bearer ${token}`);
    if (!headers.has("Content-Type") && opt.body) headers.set("Content-Type", "application/json");
    const r = await fetch(url, { ...opt, headers });
    if (r.status === 401 || r.status === 403) {
      alert("Tu sesión expiró o no tienes permisos. Vuelve a iniciar sesión.");
      try { window.Auth?.logout?.(); } catch {}
      location.href = "index.html";
      throw new Error("UNAUTHORIZED");
    }
    return r;
  }

  // ---------- API ----------
  async function apiList() {
    const r = await authFetch("/api/clientes");
    if (!r.ok) throw new Error(await readErr(r));
    return await r.json();
  }

  async function apiKpis() {
    const r = await authFetch("/api/clientes/kpis");
    if (!r.ok) throw new Error(await readErr(r));
    return await r.json();
  }

  async function apiCreate(body) {
    const r = await authFetch("/api/clientes", {
      method: "POST",
      body: JSON.stringify(body),
    });
    if (!r.ok) throw new Error(await readErr(r));
    // puede ser 201 con JSON o vacío
    return await r.json().catch(() => null);
  }

  async function apiUpdate(doc, body) {
    const r = await authFetch(`/api/clientes/${encodeURIComponent(doc)}`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
    if (!r.ok) throw new Error(await readErr(r));
    return await r.json().catch(() => null);
  }

  async function apiDelete(doc) {
    const r = await authFetch(`/api/clientes/${encodeURIComponent(doc)}`, { method: "DELETE" });
    if (!r.ok) throw new Error(await readErr(r));
    return null;
  }

  async function apiReniec(dni) {
    const r = await authFetch(`/api/reniec/dni/${encodeURIComponent(dni)}`);
    if (!r.ok) throw new Error(await readErr(r));
    return await r.json();
  }

  // ---------- UI render ----------
  function renderTabla() {
    if (!tbody) return;

    const q = (filtroTxt?.value || "").toLowerCase().trim();
    const ord = filtroOrden?.value || "nombre";

    let data = CLIENTES.slice();

    if (q) {
      data = data.filter((c) => {
        const doc = docOf(c).toLowerCase();
        const nom = nomOf(c).toLowerCase();
        const tel = telOf(c).toLowerCase();
        const em = mailOf(c).toLowerCase();
        return doc.includes(q) || nom.includes(q) || tel.includes(q) || em.includes(q);
      });
    }

    if (ord === "nombre") data.sort((a, b) => nomOf(a).localeCompare(nomOf(b)));
    if (ord === "puntos") data.sort((a, b) => puntosOf(b) - puntosOf(a));
    if (ord === "compras") data.sort((a, b) => comprasOf(b) - comprasOf(a));
    if (ord === "recientes") data.sort((a, b) => {
      const aa = parseDate(a?.ultimaCompra)?.getTime() || 0;
      const bb = parseDate(b?.ultimaCompra)?.getTime() || 0;
      return bb - aa;
    });

    tbody.innerHTML = "";
    for (const c of data) {
      const doc = docOf(c);
      const ult = parseDate(c?.ultimaCompra);
      const ultTxt = ult ? ult.toLocaleString("es-PE") : "—";

      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${doc}</td>
        <td>${nomOf(c) || "-"}</td>
        <td>${telOf(c) || "-"}</td>
        <td>${mailOf(c) || "-"}</td>
        <td class="text-end">${comprasOf(c)}</td>
        <td class="text-end">${puntosOf(c)}</td>
        <td>${ultTxt}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary me-1"
            data-bs-toggle="modal" data-bs-target="#modalCli"
            onclick="openCli('${doc.replace(/'/g, "\\'")}')">Editar</button>
          <button class="btn btn-sm btn-outline-danger"
            onclick="delCli('${doc.replace(/'/g, "\\'")}')">Eliminar</button>
        </td>
      `;
      tbody.appendChild(tr);
    }
  }

  function renderKpisFromServer(k) {
    kpiClientes.textContent = k?.clientes ?? CLIENTES.length;
    kpiConCompras.textContent = k?.conCompras ?? CLIENTES.filter(x => comprasOf(x) > 0).length;
    kpiPuntosTotales.textContent = k?.puntosTotales ?? CLIENTES.reduce((s, x) => s + puntosOf(x), 0);
    // tu UI muestra S/ 0.00 si no calculas ticket
    kpiTicketProm.textContent = money(k?.ticketPromHist ?? 0);
  }

  // ---------- Loaders ----------
  async function refresh() {
    const [lista, kpis] = await Promise.all([apiList(), apiKpis().catch(() => null)]);
    CLIENTES = Array.isArray(lista) ? lista : [];
  
    // Enriquecer cada cliente con compras y última compra
    CLIENTES = CLIENTES.map(c => {
      const info = calcularComprasCliente(c.doc);
      return {
        ...c,
        compras: info.compras,
        ultimaCompra: info.ultima
      };
    });
  
    // 🔥 Esto faltaba
    renderTabla();
    renderKpisFromServer(kpis);
  }
  

  // ---------- Modal ----------
  function openCli(doc) {
    form.classList.remove("was-validated");
    form.reset();

    editDoc.value = "";
    cDoc.disabled = false;

    if (!doc) {
      titleCli.textContent = "Nuevo cliente";
      cPts.value = 0;
      return;
    }

    const c = CLIENTES.find((x) => docOf(x) === String(doc));
    if (!c) return;

    titleCli.textContent = "Editar cliente";
    editDoc.value = docOf(c);

    cDoc.value = docOf(c);
    cDoc.disabled = true;

    cNom.value = nomOf(c);
    cTel.value = telOf(c);
    cMail.value = mailOf(c);
    cPts.value = puntosOf(c);
  }

  async function delCli(doc) {
    if (!confirm("¿Eliminar cliente " + doc + "?")) return;
    await apiDelete(doc);
    await refresh();
  }

  window.openCli = openCli;
  window.delCli = delCli;

  // ---------- RENIEC autofill en el modal ----------
  let reniecTimer = null;
  cDoc?.addEventListener("input", () => {
    if (cDoc.disabled) return; // edit mode
    const dni = cDoc.value.trim();
    // solo DNI de 8
    if (!/^\d{0,8}$/.test(dni)) return;

    clearTimeout(reniecTimer);
    if (dni.length !== 8) return;

    reniecTimer = setTimeout(async () => {
      try {
        const dto = await apiReniec(dni);
        // dto: {nombres, apellidoPaterno, apellidoMaterno} según ReniecDto
        const full = [dto.apellidoPaterno, dto.apellidoMaterno, dto.nombres].filter(Boolean).join(" ").replace(/\s+/g, " ").trim();
        if (full) cNom.value = full;
      } catch (e) {
        // no molestes: solo si quieres, puedes avisar
        // console.warn("RENIEC:", e.message || e);
      }
    }, 350);
  });

  // ---------- Submit (Create/Update) ----------
  form?.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }

    const editing = (editDoc.value || "").trim();
    const doc = cDoc.value.trim();

    const body = {
      doc,
      nombre: cNom.value.trim(),
      telefono: cTel.value.trim() || null,
      email: cMail.value.trim() || null,
      puntos: Number(cPts.value || 0) || 0,
      // opcional: no fuerces compras/ultimaCompra acá si lo maneja ventas
      activo: true,
    };

    try {
      if (!editing) {
        // si ya existe, mejor actualizar (evita "duplicado")
        const exists = CLIENTES.some((c) => docOf(c) === doc);
        if (exists) {
          const ok = confirm("Ese DNI/Doc ya existe. ¿Quieres ACTUALIZARLO?");
          if (!ok) return;
          await apiUpdate(doc, body);
        } else {
          await apiCreate(body);
        }
      } else {
        await apiUpdate(editing, body);
      }

      const modal = window.bootstrap?.Modal.getInstance(modalEl);
      modal?.hide();

      await refresh();
    } catch (err) {
      alert("Error al guardar cliente: " + (err.message || err));
    }
  });

  // ---------- Export CSV ----------
  btnExport?.addEventListener("click", () => {
    const rows = [["doc","nombre","telefono","email","compras","puntos","ultimaCompra"]];
    CLIENTES.forEach((c) => rows.push([
      docOf(c), nomOf(c), telOf(c), mailOf(c),
      comprasOf(c), puntosOf(c),
      c?.ultimaCompra ?? ""
    ]));
    const csv = rows.map(r => r.map(x => `"${String(x ?? "").replace(/"/g,'""')}"`).join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "clientes.csv";
    a.click();
  });

  filtroTxt?.addEventListener("input", renderTabla);
  filtroOrden?.addEventListener("change", renderTabla);

  document.addEventListener("DOMContentLoaded", () => {
    refresh().catch((e) => {
      // si falla por sesion, authFetch ya redirige
      console.error(e);
    });
  });
})();
