let allReports = [];
let currentView = 'grid';

const TYPE_BADGE = {
  STATEMENT: ['badge-statement', 'STATEMENT'],
  TAX_DOC:   ['badge-taxdoc',    'TAX DOC'],
  ANALYSIS:  ['badge-analysis',  'ANALYSIS'],
};

const REPORT_ICONS = {
  STATEMENT: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>`,
  TAX_DOC:   `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>`,
  ANALYSIS:  `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/></svg>`,
};

async function loadReports() {
  allReports = await apiFetch('/reports') || [];

  const years = [...new Set(allReports.map(r => new Date(r.date).getFullYear()))].sort((a, b) => b - a);
  const yearSel = document.getElementById('year-filter');
  yearSel.innerHTML = '<option value="">All Years</option>' +
    years.map(y => `<option value="${y}">${y}</option>`).join('');

  document.getElementById('download-all-label').textContent = `Download All (${allReports.length})`;
  renderReports(allReports);
}

function filterReports() {
  const type = document.getElementById('doc-type-filter').value;
  const year = document.getElementById('year-filter').value;
  const filtered = allReports.filter(r => {
    const matchType = !type || r.type === type;
    const matchYear = !year || new Date(r.date).getFullYear() == year;
    return matchType && matchYear;
  });
  renderReports(filtered);
}

function renderReports(reports) {
  const container = document.getElementById('reports-container');
  container.className = currentView === 'grid' ? 'reports-grid' : '';

  if (!reports.length) {
    container.innerHTML = '<div class="empty" style="grid-column:1/-1"><p>No documents found</p></div>';
    if (typeof uiAnimateFadeIn === 'function') {
      uiAnimateFadeIn(container.querySelectorAll('.empty'));
    }
    return;
  }

  container.innerHTML = reports.map(r => {
    const [badgeClass, badgeLabel] = TYPE_BADGE[r.type] || ['badge-other', r.type];
    const icon = REPORT_ICONS[r.type] || REPORT_ICONS.ANALYSIS;
    const sizeStr = r.fileSizeBytes ? (r.fileSizeBytes / (1024 * 1024)).toFixed(1) + ' MB' : '—';
    return `
    <div class="report-card">
      <div class="report-card-top">
        <div class="report-icon">${icon}</div>
        <span class="report-type-badge ${badgeClass}">${badgeLabel}</span>
      </div>
      <div class="report-title">${r.title}</div>
      <div class="report-desc">${r.description}</div>
      <div class="report-meta">
        <div class="report-meta-info">
          <span class="report-date">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
            ${fmtDate(r.date)}
          </span>
          <span class="report-size">
            <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            ${sizeStr}
          </span>
        </div>
        <button class="dl-btn" onclick="downloadReport('${r.id}', '${r.title}')" title="Download">
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        </button>
      </div>
    </div>`;
  }).join('');

  if (typeof uiAnimateFadeIn === 'function') {
    uiAnimateFadeIn(container.querySelectorAll('.report-card'));
  }
}

function setView(view) {
  currentView = view;
  document.getElementById('grid-view-btn').classList.toggle('active', view === 'grid');
  document.getElementById('list-view-btn').classList.toggle('active', view === 'list');
  filterReports();
}

async function downloadReport(id, title) {
  try {
    const r = await fetch(`${API_BASE}/reports/${id}/download`);
    if (!r.ok) throw new Error('Download failed');
    const blob = await r.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = title + '.pdf'; a.click();
    URL.revokeObjectURL(url);
    showToast('Downloading ' + title);
  } catch {
    showToast('Download failed', false);
  }
}

async function downloadAll() {
  showToast('Preparing all documents for download…');
  try {
    const r = await fetch(`${API_BASE}/reports/download-all`);
    if (!r.ok) throw new Error();
    const blob = await r.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = 'all-reports.zip'; a.click();
    URL.revokeObjectURL(url);
  } catch {
    showToast('Bulk download failed', false);
  }
}

function loadMoreReports() {
  showToast('Loading more documents…', false);
}

document.addEventListener('DOMContentLoaded', loadReports);
