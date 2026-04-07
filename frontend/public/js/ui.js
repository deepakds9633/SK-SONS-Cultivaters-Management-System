/**
 * SK SONS Cultivators Management System
 * Shared UI Logic for Responsiveness & Interactions
 */

document.addEventListener('DOMContentLoaded', () => {
    initSidebar();
    initTopbarTime();
    initTopbarNotificationBell();
    loadEnquiryBadge();
    // Poll every 30 seconds for real-time notification updates
    setInterval(loadEnquiryBadge, 30000);
});

/**
 * Sidebar Toggle Logic for Mobile
 */
function initSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const menuToggle = document.querySelector('.menu-toggle');
    const overlay = document.querySelector('.sidebar-overlay');
    const closeBtn = document.querySelector('.sidebar-close');

    if (!sidebar) return;

    const toggleSidebar = () => {
        sidebar.classList.toggle('open');
        if (overlay) overlay.classList.toggle('active');
        document.body.style.overflow = sidebar.classList.contains('open') ? 'hidden' : '';
    };

    if (menuToggle) menuToggle.addEventListener('click', toggleSidebar);
    if (overlay) overlay.addEventListener('click', toggleSidebar);
    if (closeBtn) closeBtn.addEventListener('click', toggleSidebar);

    // Close sidebar on link click (mobile)
    const navLinks = sidebar.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (window.innerWidth <= 768 && sidebar.classList.contains('open')) {
                toggleSidebar();
            }
        });
    });
}

/**
 * Topbar Digital Clock
 */
function initTopbarTime() {
    const timeEl = document.getElementById('topbar-time');
    if (!timeEl) return;

    const updateTime = () => {
        const now = new Date();
        timeEl.textContent = now.toLocaleTimeString('en-IN', {
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: true
        });
    };

    updateTime();
    setInterval(updateTime, 1000);
}

// Global modal helpers if needed across pages
window.openModal = function(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.add('open');
        document.body.style.overflow = 'hidden';
    }
};

window.closeModal = function(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.remove('open');
        document.body.style.overflow = '';
    }
};

// Global format currency
window.formatCurrency = function(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2
    }).format(amount);
};

/**
 * Inject a notification bell + dropdown popup into the topbar-right on every page.
 * On click: shows dropdown with recent enquiries, marks all as read, clears badge.
 */
function initTopbarNotificationBell() {
    const topbarRight = document.querySelector('.topbar-right');
    if (!topbarRight || document.getElementById('notifWrapper')) return;

    // ── Wrapper (positions dropdown relative to bell) ──
    const wrapper = document.createElement('div');
    wrapper.id = 'notifWrapper';
    wrapper.className = 'notif-wrapper';

    // ── Bell button ──
    const btn = document.createElement('button');
    btn.id = 'topbarNotifBtn';
    btn.className = 'topbar-notif-btn';
    btn.title = 'Customer Enquiries';
    btn.setAttribute('aria-label', 'View enquiry notifications');
    btn.innerHTML = `
        <span class="notif-bell">🔔</span>
        <span class="notif-topbar-count" id="topbarNotifCount" style="display:none;"></span>
    `;
    btn.addEventListener('click', (e) => { e.stopPropagation(); _toggleNotifDropdown(); });

    // ── Dropdown panel ──
    const dropdown = document.createElement('div');
    dropdown.id = 'notifDropdown';
    dropdown.className = 'notif-dropdown';
    dropdown.innerHTML = `
        <div class="notif-dropdown-header">
            <div class="notif-dropdown-title" id="notifDropdownTitle">🔔 Enquiries</div>
            <a href="enquiries.html" class="notif-view-all">View All →</a>
        </div>
        <div class="notif-dropdown-body" id="notifDropdownBody">
            <div class="notif-loading">⏳ Loading...</div>
        </div>
        <div class="notif-dropdown-footer">
            <a href="enquiries.html" class="notif-footer-link">📨 Open Enquiries Page →</a>
        </div>
    `;

    wrapper.appendChild(btn);
    wrapper.appendChild(dropdown);
    topbarRight.insertBefore(wrapper, topbarRight.firstChild);

    // Close when clicking anywhere outside the wrapper
    document.addEventListener('click', function(e) {
        if (!wrapper.contains(e.target)) {
            _closeNotifDropdown();
        }
    });
}

/**
 * Enquiry Notification Badge
 * Fetches unread count from backend and updates:
 *  1) All sidebar nav badges (.enquiry-nav-badge)
 *  2) The topbar notification bell count (#topbarNotifCount)
 */
window.loadEnquiryBadge = async function() {
    try {
        const res = await fetch(`${typeof API_HOST !== 'undefined' ? API_HOST : ''}/api/enquiries/unread-count`);
        if (!res.ok) return;
        const data = await res.json();
        const count = data.count || 0;
        const label = count > 99 ? '99+' : String(count);

        // ── Update sidebar nav badges ──
        document.querySelectorAll('.enquiry-nav-badge').forEach(badge => {
            if (count > 0) {
                badge.textContent = label;
                badge.style.display = 'inline-flex';
            } else {
                badge.style.display = 'none';
            }
        });

        // ── Update topbar bell count ──
        const topbarCount = document.getElementById('topbarNotifCount');
        const topbarBtn   = document.getElementById('topbarNotifBtn');
        if (topbarCount) {
            if (count > 0) {
                topbarCount.textContent = label;
                topbarCount.style.display = 'inline-flex';
                if (topbarBtn) topbarBtn.classList.add('has-notif');
            } else {
                topbarCount.style.display = 'none';
                if (topbarBtn) topbarBtn.classList.remove('has-notif');
            }
        }
    } catch (e) {
        // silently fail — badges just won't show
    }
};

// ════════════════════════════════════════════════
//  NOTIFICATION DROPDOWN HELPERS
// ════════════════════════════════════════════════

async function _toggleNotifDropdown() {
    const dropdown = document.getElementById('notifDropdown');
    if (!dropdown) return;
    if (dropdown.classList.contains('open')) {
        _closeNotifDropdown();
    } else {
        await _openNotifDropdown();
    }
}

function _closeNotifDropdown() {
    const d = document.getElementById('notifDropdown');
    if (d) d.classList.remove('open');
}

async function _openNotifDropdown() {
    const dropdown = document.getElementById('notifDropdown');
    if (!dropdown) return;
    dropdown.classList.add('open');

    // Render enquiry list
    await _renderNotifItems();

    // Mark all as read then clear all badges
    try {
        const host = typeof API_HOST !== 'undefined' ? API_HOST : '';
        await fetch(`${host}/api/enquiries/mark-all-read`, { method: 'PUT' });
        document.querySelectorAll('.enquiry-nav-badge').forEach(b => b.style.display = 'none');
        const cnt = document.getElementById('topbarNotifCount');
        const btn = document.getElementById('topbarNotifBtn');
        if (cnt) cnt.style.display = 'none';
        if (btn) btn.classList.remove('has-notif');
    } catch (e) { /* silent */ }
}

async function _renderNotifItems() {
    const body  = document.getElementById('notifDropdownBody');
    const title = document.getElementById('notifDropdownTitle');
    if (!body) return;

    body.innerHTML = `<div class="notif-loading"><span class="notif-spin-icon">🔄</span> Fetching enquiries...</div>`;

    try {
        const host = typeof API_HOST !== 'undefined' ? API_HOST : '';
        const res  = await fetch(`${host}/api/enquiries`);
        if (!res.ok) throw new Error('fetch failed');
        const enquiries = await res.json();

        // Count unread — backend field is "isRead" (enforced via @JsonProperty)
        const unread = enquiries.filter(e => (e.isRead || false) === false).length;
        if (title) {
            title.innerHTML = unread > 0
                ? `🔔 <span class="notif-new-label">${unread} New</span> Enquir${unread === 1 ? 'y' : 'ies'}`
                : `🔔 Recent Enquiries`;
        }

        if (!enquiries.length) {
            body.innerHTML = `
                <div class="notif-empty">
                    <span class="notif-empty-icon">📭</span>
                    <p>No enquiries yet</p>
                    <span>Customers haven't written in yet.</span>
                </div>`;
            return;
        }

        // Show up to 5 most recent
        const recent = enquiries.slice(0, 5);
        body.innerHTML = recent.map((eq, i) => {
            const name    = eq.name    || 'Unknown';
            const msg     = eq.message ? (eq.message.length > 72 ? eq.message.substring(0, 72) + '\u2026' : eq.message) : 'No message';
            const phone   = eq.phone   || '';
            const village = eq.village || '';
            const isUnread = !(eq.isRead || false);   // false = unread; missing field = treat as unread
            const timeStr  = eq.submissionDate ? _timeAgo(new Date(eq.submissionDate)) : '';
            const initial  = name.charAt(0).toUpperCase();
            const metaParts = [phone ? '\uD83D\uDCDE ' + phone : '', village ? '\uD83D\uDCCD ' + village : ''].filter(Boolean);

            return `
                <a href="enquiries.html" class="notif-item${isUnread ? ' unread' : ''}" style="--delay:${i * 0.05}s">
                    <div class="notif-avatar${isUnread ? ' unread-avatar' : ''}">${initial}</div>
                    <div class="notif-item-content">
                        <div class="notif-item-row">
                            <span class="notif-item-name">${name}</span>
                            ${isUnread ? '<span class="notif-unread-dot"></span>' : ''}
                            <span class="notif-item-time">${timeStr}</span>
                        </div>
                        ${metaParts.length ? `<div class="notif-item-meta">${metaParts.join(' &nbsp;·&nbsp; ')}</div>` : ''}
                        <p class="notif-item-msg">${msg}</p>
                    </div>
                </a>`;
        }).join('');

        if (enquiries.length > 5) {
            body.innerHTML += `<div class="notif-more">+${enquiries.length - 5} more — <a href="enquiries.html">View all</a></div>`;
        }

    } catch (e) {
        body.innerHTML = `<div class="notif-empty"><p style="color:var(--danger)">⚠️ Could not load enquiries.<br>Check backend connection.</p></div>`;
    }
}

function _timeAgo(date) {
    const diff = Math.floor((Date.now() - date) / 1000);
    if (diff < 60)    return 'just now';
    if (diff < 3600)  return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}d ago`;
    return date.toLocaleDateString('en-IN', { day: '2-digit', month: 'short' });
}
