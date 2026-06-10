const DONUT_COLORS = ['#1a6b3c', '#4ade80', '#86efac', '#bbf7d0', '#dcfce7'];

async function loadPortfolio() {
  const data = await apiFetch('/portfolio/summary');

  if (data?.error) {
    showToast(data.error, false);
    return;
  }
  if (!data) return;

  document.getElementById('total-portfolio-value').textContent = fmt(data.totalValue ?? 0);
  document.getElementById('portfolio-growth').textContent =
    (data.growthPercent >= 0 ? '+' : '') + (data.growthPercent ?? 0).toFixed(1) + '%';

  renderBarChart(data.chartData ?? []);
  renderDonut(data.allocations ?? []);
  document.getElementById('fund-count').textContent = data.fundCount ?? '—';

  const tbody = document.getElementById('perf-table-body');
  tbody.innerHTML = (data.performanceByClass ?? []).map(row => `
    <tr>
      <td>${row.name}</td>
      <td style="font-family:var(--font-mono);text-align:right">${fmt(row.committed)}</td>
      <td style="font-family:var(--font-mono);text-align:right">${fmt(row.invested)}</td>
      <td class="irr-green">${row.irr.toFixed(1)}%</td>
    </tr>`).join('') || '<tr><td colspan="4" style="text-align:center;padding:20px;color:var(--muted)">No data</td></tr>';

  const productsBody = document.getElementById('products-table-body');
  if (productsBody) {
    productsBody.innerHTML = (data.products ?? []).map(p => `
      <tr>
        <td style="font-weight:500">${p.name}</td>
        <td>${p.assetClass}</td>
        <td style="font-family:var(--font-mono);text-align:right">${fmt(p.committed)}</td>
        <td style="font-family:var(--font-mono);text-align:right">${fmt(p.invested)}</td>
        <td style="font-family:var(--font-mono);text-align:right">${fmt(p.currentValue)}</td>
        <td class="irr-green">${p.irr.toFixed(1)}%</td>
      </tr>`).join('') || '<tr><td colspan="6" style="text-align:center;padding:20px;color:var(--muted)">No products</td></tr>';
  }

  const actEl = document.getElementById('activity-list');
  actEl.innerHTML = (data.recentActivity ?? []).map(a => {
    const pos = a.amount >= 0;
    return `<div class="activity-item">
      <div class="activity-icon">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          ${pos ? '<polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/>'
                : '<rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/>'}
        </svg>
      </div>
      <div style="flex:1;min-width:0">
        <div class="activity-title">${a.type} <span>from ${a.source}</span></div>
        <div class="activity-meta">${fmtDate(a.date)} · <span class="${pos ? 'activity-amount-pos' : 'activity-amount-neg'}">${pos ? '+' : ''}${fmtFull(a.amount)}</span></div>
      </div>
    </div>`;
  }).join('') || '<div class="empty"><p>No recent activity</p></div>';
}

function renderBarChart(data) {
  const el = document.getElementById('bar-chart');
  if (!data.length) { el.innerHTML = ''; return; }
  const max = Math.max(...data.map(d => d.value));
  el.innerHTML = data.map((d, i) => {
    const h = Math.max(8, Math.round((d.value / max) * 100));
    const hi = i >= data.length - 3;
    return `<div class="bar ${hi ? 'highlighted' : ''}" style="height:${h}%" title="${d.label}: ${fmt(d.value)}"></div>`;
  }).join('');
}

function renderDonut(allocs) {
  const legend = document.getElementById('alloc-legend');
  if (!allocs.length) { legend.innerHTML = '<div class="empty"><p>No allocation data</p></div>'; return; }
  let offset = 25;
  const r = 15.915, circ = 2 * Math.PI * r;
  const paths = allocs.map((a, i) => {
    const dash = (a.percent / 100) * circ;
    const gap = circ - dash;
    const el = `<circle cx="18" cy="18" r="${r}" fill="transparent"
      stroke="${DONUT_COLORS[i % DONUT_COLORS.length]}" stroke-width="4"
      stroke-dasharray="${dash} ${gap}"
      stroke-dashoffset="${-((offset / 100) * circ - circ / 4)}"
      transform="rotate(-90 18 18)"/>`;
    offset += a.percent;
    return el;
  });
  document.getElementById('donut-chart').innerHTML =
    `<circle cx="18" cy="18" r="${r}" fill="transparent" stroke="var(--border)" stroke-width="4"/>` + paths.join('');

  legend.innerHTML = allocs.map((a, i) =>
    `<div class="alloc-item">
      <span class="alloc-dot" style="background:${DONUT_COLORS[i % DONUT_COLORS.length]}"></span>
      <span>${a.label}</span>
      <span class="alloc-pct">${a.percent}%</span>
    </div>`).join('');
}

function setTimeRange(range, btn) {
  document.querySelectorAll('.time-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
}

document.addEventListener('DOMContentLoaded', loadPortfolio);
