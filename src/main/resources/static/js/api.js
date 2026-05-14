// ============================
// api.js — Centralized fetch wrapper
// ============================
const API_BASE = '/api';

function getToken() {
    return localStorage.getItem('lh_token');
}

async function apiFetch(endpoint, options = {}) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const response = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers
    });

    if (response.status === 401) {
        localStorage.clear();
        window.location.href = '/login.html';
        return;
    }

    const text = await response.text();
    let data;
    try { data = text ? JSON.parse(text) : null; } catch { data = text; }

    if (!response.ok) {
        const msg = data?.message || data?.error || 'Request failed';
        throw new Error(msg);
    }
    return data;
}

const api = {
    get: (endpoint) => apiFetch(endpoint, { method: 'GET' }),
    post: (endpoint, body) => apiFetch(endpoint, { method: 'POST', body: JSON.stringify(body) }),
    put: (endpoint, body) => apiFetch(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
    delete: (endpoint) => apiFetch(endpoint, { method: 'DELETE' }),
};

// ============================
// Toast notifications
// ============================
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const icons = { success: '✅', error: '❌', info: 'ℹ️' };
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${icons[type]}</span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

// ============================
// Modal helpers
// ============================
function openModal(id) {
    document.getElementById(id)?.classList.add('open');
}
function closeModal(id) {
    document.getElementById(id)?.classList.remove('open');
}

// ============================
// Tabs
// ============================
function initTabs() {
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', () => {
            const target = tab.dataset.tab;
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            tab.classList.add('active');
            document.getElementById(target)?.classList.add('active');
        });
    });
}

// ============================
// Format helpers
// ============================
function formatPrice(amount, isFree) {
    if (isFree || amount === 0 || amount === null) return '<span class="course-price free">FREE</span>';
    return `<span class="course-price">৳${parseFloat(amount).toFixed(2)}</span>`;
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
}

function statusBadge(status) {
    return status
        ? '<span class="badge badge-success">Active</span>'
        : '<span class="badge badge-danger">Inactive</span>';
}

function roleBadge(role) {
    const map = { ADMIN: 'badge-primary', TEACHER: 'badge-amber', STUDENT: 'badge-success' };
    return `<span class="badge ${map[role] || 'badge-primary'}">${role}</span>`;
}

window.api = api;
window.showToast = showToast;
window.openModal = openModal;
window.closeModal = closeModal;
window.initTabs = initTabs;
window.formatPrice = formatPrice;
window.formatDate = formatDate;
window.statusBadge = statusBadge;
window.roleBadge = roleBadge;
