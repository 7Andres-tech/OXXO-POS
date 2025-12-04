// /resources/static/js/clientes.js
(() => {
  "use strict";

  
  // ============ Helpers ============
  const $ = (s) => document.querySelector(s);
  const PEN = new Intl.NumberFormat("es-PE", { style: "currency", currency: "PEN" });
  const money = (n) => PEN.format(Number(n || 0));

  const docOf = (c) => String(c.doc ?? c.dni ?? c.documento ?? c.id ?? "").trim();
  const nomOf = (c) => String(c.nombre ?? c.name ?? "").trim();
  const telOf = (c) => String(c.telefono ?? c.phone ?? "").trim();
  const mailOf = (c) => String(c.email ?? "").trim();
  const comprasOf = (c) => Number(c.compras ?? 0) || 0;
  const puntosOf = (c) => Number(c.puntos ?? 0) || 0;
  const ultOf = (c) => c.ultimaCompra ?? c.ultima_compra ?? null;

  function parseDate(d) {
    if (!d) return null;
    const x = new Date(d);
    return isNaN(x.getTime()) ? null : x;
  }

  async function readErr(r) {
    // intenta leer JSON de Spring: {message,error,...}
    const txt = await r.text().catch(() => "");
    if (!txt) return `${r.status} ${r.statusText || "Error"}`;
    try {
      const j = JSON.parse(txt);
      return j.message || j.error || txt;
    } catch {
      return txt;
    }
  }

  // ============ DOM ============
  const tbody = $("#cliBody");
  const filtroTxt = $("#filtroTxt");
  const filtroOrden = $("#filtroOrden");
  const btnExport = $("#btnExport");

  const kpiClientes = $("#kpiClientes");
  const kpiConCompras = $("#kpiConCompras");
  const kpiPuntosTotales = $("#kpiPuntosTotales");
  const kpiTicketProm = $("#kpiTicketProm");

  const modalEl = $("#modalCli");
  const form = $("#formCli");
  const titleCli = $("#titleCli");
  const editDoc = $("#editDoc");
  const cDoc = $("#cDoc");
  const cNom = $("#cNom");
  const cTel = $("#cTel");
  const cMail = $("#cMail");
  const cPts = $("#cPts");

  // ============ Auth ============
  // (si tu HTML ya llama Auth.requireAuth, esto igual no estorba)
  if (window.Auth?.requireAuth) Auth.requireAuth(["ADMIN", "CAJERO"]);

  // ============ API layer ============
  const HAS_API = !!(window.API?.clientes?.list);

  async function apiList() {
    if (HAS_API) return await API.clientes.list();
    const r = await fetch("/api/clientes");
    if (!r.ok) throw new Error(await readErr(r));
    return await r.json();
  }

  async function apiCreate(body) {
    if (HAS_API && API.clientes.crear) return await API.clientes.crear(body);
    const r = await fetch("/api/clientes", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!r.ok) throw new Error(await readErr(r));
    return r.status === 204 ? null : await r.json().catch(() => null);
  }

  async function apiUpdate(doc, body) {
    if (HAS_API && API.clientes.actualizar) return await API.clientes.actualizar(doc, body);

    // intento PUT REST
    let r = await fetch(`/api/clientes/${encodeURIComponent(doc)}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    // fallback por si tu backend no tiene PUT (muchos profes lo hacen con POST upsert)
    if (r.status === 404 || r.status === 405) {
      r = await fetch("/api/clientes", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
    }

    if (!r.ok) throw new Error(await readErr(r));
    return r.status === 204 ? null : await r.json().catch(() => null);
  }

  async function apiDelete(doc) {
    if (HAS_API && API.clientes.del) return await API.clientes.del(doc);
    const r = await fetch(`/api/clientes/${encodeURIComponent(doc)}`, { method: "DELETE" });
    if (!r.ok) throw new Error(await readErr(r));
    return r.status === 204 ? null : await r.json().catch(() => null);
  }

  // ============ State ============
  let CLIENTES = [];

  function renderKpisLocal() {
    const total = CLIENTES.length;
    const conCompras = CLIENTES.filter((c) => comprasOf(c) > 0).length;
    const puntosTot = CLIENTES.reduce((s, c) => s + puntosOf(c), 0);
    kpiClientes.textContent = total;
    kpiConCompras.textContent = conCompras;
    kpiPuntosTotales.textContent = puntosTot;
    kpiTicketProm.textContent = money(0);
  }

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
    if (ord === "recientes") data.sort((a, b) => (parseDate(ultOf(b))?.getTime() || 0) - (parseDate(ultOf(a))?.getTime() || 0));

    tbody.innerHTML = "";
    for (const c of data) {
      const doc = docOf(c);
      const ult = parseDate(ultOf(c));
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

  async function refresh() {
    try {
      const j = await apiList();
      CLIENTES = Array.isArray(j) ? j : (Array.isArray(j?.content) ? j.content : []);
      renderTabla();
      renderKpisLocal();
    } catch (e) {
      console.error(e);
      alert("No se pudo cargar clientes: " + (e.message || e));
    }
  }

  // ============ Modal ============
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
    try {
      await apiDelete(doc);
      await refresh();
    } catch (e) {
      alert("No se pudo eliminar: " + (e.message || e));
    }
  }

  window.openCli = openCli;
  window.delCli = delCli;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }

    const editing = (editDoc.value || "").trim();
    const doc = cDoc.value.trim();

    // Body compatible (manda doc + dni + documento por si tu backend usa otro nombre)
    const body = {
      doc,
      dni: doc,
      documento: doc,
      nombre: cNom.value.trim(),
      telefono: cTel.value.trim() || null,
      email: cMail.value.trim() || null,
      puntos: Number(cPts.value || 0) || 0,
      compras: 0,
      activo: true,
    };

    try {
      // ✅ Si estás en "Nuevo" pero ese doc YA existe => hace UPDATE (evita error por duplicado)
      if (!editing) {
        const exists = CLIENTES.some((c) => docOf(c) === doc);
        if (exists) {
          const ok = confirm("Ese DNI/Doc ya existe. ¿Quieres ACTUALIZARLO en vez de crear uno nuevo?");
          if (ok) {
            await apiUpdate(doc, body);
          } else {
            return;
          }
        } else {
          await apiCreate(body);
        }
      } else {
        await apiUpdate(editing, body);
      }

      const modal = window.bootstrap?.Modal.getInstance(modalEl);
      modal?.hide();

      await refresh();
    } catch (e2) {
      console.error(e2);
      alert("Error al guardar cliente: " + (e2.message || e2));
    }
  });

  // Export CSV
  btnExport?.addEventListener("click", () => {
    const rows = [["doc","nombre","telefono","email","compras","puntos","ultimaCompra"]];
    CLIENTES.forEach((c) => rows.push([docOf(c), nomOf(c), telOf(c), mailOf(c), comprasOf(c), puntosOf(c), ultOf(c) ?? ""]));
    const csv = rows.map(r => r.map(x => `"${String(x ?? "").replace(/"/g,'""')}"`).join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = "clientes.csv";
    a.click();
  });

  filtroTxt?.addEventListener("input", renderTabla);
  filtroOrden?.addEventListener("change", renderTabla);

  document.addEventListener("DOMContentLoaded", refresh);
})();
