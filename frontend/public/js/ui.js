/**
 * SK SONS Cultivators Management System
 * Shared UI Logic for Responsiveness & Interactions
 */

document.addEventListener('DOMContentLoaded', () => {
    initSidebar();
    initTopbarTime();
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
