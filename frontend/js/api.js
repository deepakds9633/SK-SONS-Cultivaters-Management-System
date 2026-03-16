// ============================================
// SK SONS Cultivators Management System
// Shared API Utilities
// ============================================

const BASE_URL = `${API_HOST}/api`;

// Auth Guard
function checkAuth() {
    const token = sessionStorage.getItem('sk_token');
    const path = window.location.pathname;
    const isPublicPage = path.endsWith('login.html') || path.endsWith('landing.html') || path === '/' || path === '';
    if (!token && !isPublicPage) {
        window.location.href = 'login.html';
    }
}

function logout() {
    sessionStorage.removeItem('sk_token');
    window.location.href = 'login.html';
}

// ─── Theme Toggle ───────────────────────────────────────────────
function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('sk_theme', theme);
    const btn = document.getElementById('themeToggleBtn');
    if (btn) {
        btn.innerHTML = theme === 'light'
            ? '🌙 Dark Mode'
            : '☀️ Light Mode';
    }
}

function toggleTheme() {
    const current = localStorage.getItem('sk_theme') || 'dark';
    applyTheme(current === 'dark' ? 'light' : 'dark');
}

function initTheme() {
    const saved = localStorage.getItem('sk_theme') || 'dark';
    applyTheme(saved);
    // Inject toggle button into topbar-right if present
    const topbarRight = document.querySelector('.topbar-right');
    if (topbarRight && !document.getElementById('themeToggleBtn')) {
        const btn = document.createElement('button');
        btn.id = 'themeToggleBtn';
        btn.className = 'theme-toggle-btn';
        btn.onclick = toggleTheme;
        btn.innerHTML = saved === 'light' ? '🌙 Dark Mode' : '☀️ Light Mode';
        topbarRight.insertBefore(btn, topbarRight.firstChild);
    }
}


// Generic fetch helper
async function apiRequest(method, endpoint, body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
    };
    if (body) options.body = JSON.stringify(body);
    const res = await fetch(`${BASE_URL}${endpoint}`, options);
    if (!res.ok) {
        const err = await res.text();
        throw new Error(err || `HTTP ${res.status}`);
    }
    if (res.status === 204) return null;
    const text = await res.text();
    return text ? JSON.parse(text) : null;
}

const api = {
    get:    (ep)      => apiRequest('GET', ep),
    post:   (ep, b)   => apiRequest('POST', ep, b),
    put:    (ep, b)   => apiRequest('PUT', ep, b),
    delete: (ep)      => apiRequest('DELETE', ep),
};

// ─── Toast Notifications ───
function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    const icon = type === 'success' ? '✅' : '❌';
    toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// ─── Format Currency ───
function formatCurrency(amount) {
    return '₹' + (Number(amount) || 0).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    });
}

// ─── Format Date ───
function formatDate(dateStr) {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' });
}

// ─── Format Time ───
function formatTime(timeStr) {
    if (!timeStr) return '—';
    const [h, m] = timeStr.split(':');
    const hour = parseInt(h);
    const period = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${m} ${period}`;
}

// ─── Update Topbar Time ───
function startClock() {
    const el = document.getElementById('topbar-time');
    if (!el) return;
    function update() {
        const now = new Date();
        el.textContent = now.toLocaleTimeString('en-IN', {
            hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: true
        });
    }
    update();
    setInterval(update, 1000);
}

// ─── Set Active Nav Link ───
function setActiveNav() {
    const path = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href && path.endsWith(href)) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

// ─── Modal Helpers ───
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('open');
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('open');
}

// Close modal on overlay click
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('open');
    }
});

// ─── Table Search Filter ───
function filterTable(tableId, searchVal) {
    const rows = document.querySelectorAll(`#${tableId} tbody tr`);
    const val = searchVal.toLowerCase().trim();
    rows.forEach(row => {
        row.style.display = row.textContent.toLowerCase().includes(val) ? '' : 'none';
    });
}

// Init on DOM ready
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    initTheme();
    setActiveNav();
    startClock();
});
