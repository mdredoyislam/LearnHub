// ============================
// auth.js — Authentication logic
// ============================

function saveAuth(data) {
    localStorage.setItem('lh_token', data.token);
    localStorage.setItem('lh_role', data.role);
    localStorage.setItem('lh_name', data.fullName);
    localStorage.setItem('lh_email', data.email);
    localStorage.setItem('lh_userId', data.userId);
}

function getAuthUser() {
    return {
        token: localStorage.getItem('lh_token'),
        role: localStorage.getItem('lh_role'),
        name: localStorage.getItem('lh_name'),
        email: localStorage.getItem('lh_email'),
        userId: localStorage.getItem('lh_userId'),
    };
}

function isLoggedIn() {
    return !!localStorage.getItem('lh_token');
}

function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

function redirectToDashboard() {
    const role = localStorage.getItem('lh_role');
    if (role === 'ADMIN') window.location.href = '/pages/admin/dashboard.html';
    else if (role === 'TEACHER') window.location.href = '/pages/teacher/dashboard.html';
    else window.location.href = '/pages/student/dashboard.html';
}

function requireAuth(requiredRole = null) {
    if (!isLoggedIn()) { window.location.href = '/login.html'; return false; }
    if (requiredRole) {
        const role = localStorage.getItem('lh_role');
        if (role !== requiredRole) { redirectToDashboard(); return false; }
    }
    return true;
}

// ============================
// Register form handler
// ============================
async function handleRegister(e) {
    e.preventDefault();
    const btn = document.getElementById('register-btn');
    btn.disabled = true;
    btn.textContent = 'Creating Account...';
    try {
        const data = await api.post('/auth/register', {
            fullName: document.getElementById('fullName').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            phone: document.getElementById('phone').value
        });
        saveAuth(data);
        showToast('Account created! Welcome to Learn Hub 🎉', 'success');
        setTimeout(() => redirectToDashboard(), 1000);
    } catch (err) {
        showToast(err.message, 'error');
        btn.disabled = false;
        btn.textContent = 'Create Account';
    }
}

// ============================
// Login form handler
// ============================
async function handleLogin(e) {
    e.preventDefault();
    const btn = document.getElementById('login-btn');
    btn.disabled = true;
    btn.textContent = 'Signing In...';
    try {
        const data = await api.post('/auth/login', {
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        });
        saveAuth(data);
        showToast(`Welcome back, ${data.fullName}! 👋`, 'success');
        setTimeout(() => redirectToDashboard(), 800);
    } catch (err) {
        showToast(err.message, 'error');
        btn.disabled = false;
        btn.textContent = 'Sign In';
    }
}

window.saveAuth = saveAuth;
window.getAuthUser = getAuthUser;
window.isLoggedIn = isLoggedIn;
window.logout = logout;
window.redirectToDashboard = redirectToDashboard;
window.requireAuth = requireAuth;
window.handleRegister = handleRegister;
window.handleLogin = handleLogin;
