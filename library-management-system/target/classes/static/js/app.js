// 全局变量
let currentPage = 0;
let pageSize = 10;
let totalPages = 0;
let bookCurrentPage = 0;
let bookPageSize = 10;
let bookTotalPages = 0;

// 用户认证相关
let currentUser = null;
let userRole = null;
let authToken = null;

// API基础URL
const API_BASE_URL = '/api';

// 认证相关函数
function checkAuthStatus() {
    authToken = localStorage.getItem('authToken');
    if (authToken) {
        // 验证token有效性
        fetch(`${API_BASE_URL}/auth/validate`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.valid) {
                currentUser = data.username;
                userRole = data.role;
                showMainApp();
                initializeApp();
            } else {
                logout();
            }
        })
        .catch(error => {
            console.error('Token验证失败:', error);
            logout();
        });
    } else {
        showLoginForm();
        setupAuthForms();
    }
}

function showLoginForm() {
    document.getElementById('loginModal').style.display = 'block';
    document.getElementById('registerModal').style.display = 'none';
    document.getElementById('mainApp').style.display = 'none';
}

function showRegisterForm() {
    document.getElementById('loginModal').style.display = 'none';
    document.getElementById('registerModal').style.display = 'block';
    document.getElementById('mainApp').style.display = 'none';
}

function showMainApp() {
    document.getElementById('loginModal').style.display = 'none';
    document.getElementById('registerModal').style.display = 'none';
    document.getElementById('mainApp').style.display = 'block';

    // 更新用户信息显示
    document.getElementById('currentUser').textContent = currentUser || '用户';
    document.getElementById('currentUserRole').textContent = getRoleDisplayName(userRole);

    // 根据角色显示/隐藏菜单项
    updateNavigationForRole(userRole);
}

function getRoleDisplayName(role) {
    switch(role) {
        case 'ADMIN': return '系统管理员';
        case 'LIBRARIAN': return '图书管理员';
        case 'USER': return '普通用户';
        default: return '用户';
    }
}

function updateNavigationForRole(role) {
    const usersNavItem = document.getElementById('usersNavItem');
    const adminNavItem = document.getElementById('adminNavItem');

    // 只有管理员和图书管理员可以看到用户管理
    if (role === 'ADMIN' || role === 'admin' || role === 'LIBRARIAN') {
        usersNavItem.style.display = 'block';
    } else {
        usersNavItem.style.display = 'none';
    }

    // 只有系统管理员可以看到管理员功能
    if (role === 'ADMIN' || role === 'admin') {
        adminNavItem.style.display = 'block';
    } else {
        adminNavItem.style.display = 'none';
    }
}

// 显示管理员界面
function showAdminInterface() {
    console.log('显示管理员界面');

    // 显示管理员专用功能
    document.getElementById('usersNavItem').style.display = 'block';
    document.getElementById('navUserManagement').style.display = 'block';
    document.getElementById('navBookManagement').style.display = 'block';
    document.getElementById('adminNavItem').style.display = 'block';

    // 显示侧边栏管理功能
    document.getElementById('sidebarUserRegistration').style.display = 'block';
    document.getElementById('sidebarUserList').style.display = 'block';
    document.getElementById('sidebarAddBook').style.display = 'block';
    document.getElementById('sidebarBookList').style.display = 'block';

    // 隐藏普通用户专用功能
    document.getElementById('sidebarBrowseBooks').style.display = 'none';
    document.getElementById('sidebarMyFavorites').style.display = 'none';
    document.getElementById('sidebarBorrowHistory').style.display = 'none';

    // 显示仪表盘管理功能卡片
    document.getElementById('dashboardUserManagement').style.display = 'block';
    document.getElementById('dashboardBookManagement').style.display = 'block';

    // 显示注册链接（仅管理员可见）
    document.getElementById('registerLink').style.display = 'block';

    // 默认显示管理员功能区域
    setTimeout(() => {
        // 隐藏所有内容区域
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        // 移除所有导航的活动状态
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        // 显示仪表盘作为管理员的主界面
        const dashboardNavItem = document.querySelector('a[href="#dashboard"]').parentElement;
        const dashboardSection = document.getElementById('dashboard');

        if (dashboardNavItem && dashboardSection) {
            dashboardNavItem.classList.add('active');
            dashboardSection.classList.add('active');

            // 加载管理员统计数据
            loadStatistics();
        }
    }, 100);
}

// 显示普通用户界面
function showUserInterface() {
    console.log('显示普通用户界面');

    // 隐藏管理员专用功能
    document.getElementById('usersNavItem').style.display = 'none';
    document.getElementById('navUserManagement').style.display = 'none';
    document.getElementById('navBookManagement').style.display = 'none';
    document.getElementById('adminNavItem').style.display = 'none';

    // 隐藏侧边栏管理功能
    document.getElementById('sidebarUserRegistration').style.display = 'none';
    document.getElementById('sidebarUserList').style.display = 'none';
    document.getElementById('sidebarAddBook').style.display = 'none';
    document.getElementById('sidebarBookList').style.display = 'none';

    // 显示普通用户专用功能
    document.getElementById('sidebarBrowseBooks').style.display = 'block';
    document.getElementById('sidebarMyFavorites').style.display = 'block';
    document.getElementById('sidebarBorrowHistory').style.display = 'block';

    // 隐藏仪表盘管理功能卡片
    document.getElementById('dashboardUserManagement').style.display = 'none';
    document.getElementById('dashboardBookManagement').style.display = 'none';

    // 确保注册链接隐藏
    document.getElementById('registerLink').style.display = 'none';

    // 默认显示图书列表区域
    setTimeout(() => {
        // 隐藏所有内容区域
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        // 移除所有导航的活动状态
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        // 显示仪表盘作为用户的主界面，但不显示管理功能
        const dashboardNavItem = document.querySelector('a[href="#dashboard"]').parentElement;
        const dashboardSection = document.getElementById('dashboard');

        if (dashboardNavItem && dashboardSection) {
            dashboardNavItem.classList.add('active');
            dashboardSection.classList.add('active');

            // 加载用户统计数据
            loadStatistics();
        }
    }, 100);
}

function setupAuthForms() {
    // 登录表单
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;

            fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 调试：打印返回的数据结构
                    console.log('登录返回数据:', data);
                    console.log('userType:', data.userType);
                    console.log('user object:', data.user);

                    // 使用sessionId作为token
                    authToken = data.sessionId;
                    currentUser = data.user.username;
                    // 从user对象中获取role，或从userType中获取
                    userRole = data.userType || data.user.role;

                    console.log('设置的用户角色:', userRole);

                    localStorage.setItem('authToken', authToken);
                    localStorage.setItem('currentUser', currentUser);
                    localStorage.setItem('userRole', userRole);

                    showMessage('登录成功！', 'success');
                    showMainApp();
                    initializeApp();
                } else {
                    showMessage(data.message || '登录失败', 'error');
                }
            })
            .catch(error => {
                console.error('登录错误:', error);
                showMessage('登录失败，请检查网络连接', 'error');
            });
        });
    }

    // 注册表单
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const password = document.getElementById('regPassword').value;
            const confirmPassword = document.getElementById('regConfirmPassword').value;

            if (password !== confirmPassword) {
                showMessage('两次输入的密码不一致', 'error');
                return;
            }

            const formData = {
                username: document.getElementById('regUsername').value,
                password: password,
                email: document.getElementById('regEmail').value,
                realName: document.getElementById('regRealName').value,
                studentId: document.getElementById('regStudentId').value,
                department: document.getElementById('regDepartment').value,
                major: document.getElementById('regMajor').value
            };

            fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 使用sessionId作为token
                    authToken = data.sessionId || '';
                    currentUser = data.user.username;
                    // 从user对象中获取role，或从userType中获取
                    userRole = data.user.role || data.userType || 'USER';

                    localStorage.setItem('authToken', authToken);
                    localStorage.setItem('currentUser', currentUser);
                    localStorage.setItem('userRole', userRole);

                    showMessage('注册成功！', 'success');
                    showMainApp();
                    initializeApp();
                } else {
                    showMessage(data.message || '注册失败', 'error');
                }
            })
            .catch(error => {
                console.error('注册错误:', error);
                showMessage('注册失败，请检查网络连接', 'error');
            });
        });
    }
}

function logout() {
    fetch(`${API_BASE_URL}/auth/logout`, {
        method: 'POST'
    })
    .then(response => response.json())
    .finally(() => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('currentUser');
        localStorage.removeItem('userRole');

        authToken = null;
        currentUser = null;
        userRole = null;

        showLoginForm();
        setupAuthForms();
        showMessage('已成功登出', 'info');
    });
}

// HTTP请求辅助函数
function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...(authToken && { 'Authorization': `Bearer ${authToken}` })
        }
    };

    return fetch(API_BASE_URL + url, { ...defaultOptions, ...options })
        .then(response => {
            if (response.status === 401) {
                // Token过期或无效
                logout();
                throw new Error('认证失败，请重新登录');
            }
            return response.json();
        });
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    checkAuthStatus();
});

// 初始化应用
function initializeApp() {
    console.log('initializeApp 被调用，当前用户角色:', userRole);

    // 导航菜单事件监听
    setupNavigation();

    // 根据用户角色显示相应界面
    if (userRole === 'ADMIN' || userRole === 'admin') {
        console.log('检测到管理员角色，显示管理员界面');
        showAdminInterface();
    } else {
        console.log('检测到普通用户角色或角色为空，显示用户界面');
        showUserInterface();
    }

    // 用户表单提交事件
    setupUserForm();

    // 加载统计数据
    loadStatistics();

    // 设置搜索功能
    setupSearch();

    // 图书管理初始化
    setupBookForm();

    // 加载图书统计
    loadBookStatistics();
}

// 设置导航菜单
function setupNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            // 移除所有活动状态
            navLinks.forEach(l => l.parentElement.classList.remove('active'));
            document.querySelectorAll('.content-section').forEach(section => {
                section.classList.remove('active');
            });

            // 添加活动状态
            this.parentElement.classList.add('active');

            // 显示对应的内容区域
            const targetId = this.getAttribute('href').substring(1);
            const targetSection = document.getElementById(targetId);
            if (targetSection) {
                targetSection.classList.add('active');
            }
        });
    });
}

// 设置用户表单
function setupUserForm() {
    const userForm = document.getElementById('userForm');
    if (userForm) {
        userForm.addEventListener('submit', function(e) {
            e.preventDefault();
            registerUser();
        });
    }
}

// 设置搜索功能
function setupSearch() {
    const searchInput = document.getElementById('userSearch');
    if (searchInput) {
        searchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                searchUsers();
            }
        });
    }
}

// 显示用户注册表单
function showUserRegistration() {
    // 切换到用户管理页面
    document.querySelector('[href="#users"]').click();

    // 显示注册表单
    const registrationDiv = document.getElementById('userRegistration');
    const listDiv = document.getElementById('userList');

    if (registrationDiv) {
        registrationDiv.style.display = 'block';
        registrationDiv.scrollIntoView({ behavior: 'smooth' });
    }

    if (listDiv) {
        listDiv.style.display = 'none';
    }
}

// 显示用户列表
function showUserList() {
    // 切换到用户管理页面
    document.querySelector('[href="#users"]').click();

    // 显示用户列表
    const registrationDiv = document.getElementById('userRegistration');
    const listDiv = document.getElementById('userList');

    if (registrationDiv) {
        registrationDiv.style.display = 'none';
    }

    if (listDiv) {
        listDiv.style.display = 'block';
        loadUserList(); // 重新加载用户列表
    }
}

// 显示图书列表（占位函数）
function showBookList() {
    document.querySelector('[href="#books"]').click();
}

// 显示借阅记录（占位函数）
function showBorrowRecords() {
    document.querySelector('[href="#records"]').click();
}

// 显示添加图书（占位函数）
function showAddBook() {
    showMessage('功能开发中', 'info');
}

// 注册用户
async function registerUser() {
    showLoading();

    try {
        const formData = new FormData(document.getElementById('userForm'));
        const userData = {};

        formData.forEach((value, key) => {
            userData[key] = value;
        });

        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });

        const result = await response.json();

        if (result.success) {
            showMessage('用户注册成功！', 'success');
            document.getElementById('userForm').reset();

            // 刷新用户列表和统计数据
            setTimeout(() => {
                loadUserList();
                loadStatistics();
            }, 1000);
        } else {
            showMessage(result.message || '注册失败', 'error');
        }
    } catch (error) {
        console.error('注册用户错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 加载用户列表
async function loadUserList(page = 0) {
    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/users?page=${page}&size=${pageSize}`);
        const result = await response.json();

        if (result.success) {
            renderUserTable(result.data);
            updatePagination(result.currentPage, result.totalPages);
            currentPage = page;
            totalPages = result.totalPages;
        } else {
            showMessage('加载用户列表失败', 'error');
        }
    } catch (error) {
        console.error('加载用户列表错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 渲染用户表格
function renderUserTable(users) {
    const tableBody = document.getElementById('userTableBody');

    if (!tableBody) return;

    if (users.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="10" class="text-center">
                    <i class="fas fa-inbox"></i> 暂无用户数据
                </td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.realName || '-'}</td>
            <td>${user.email || '-'}</td>
            <td>${user.studentId || '-'}</td>
            <td>${user.department || '-'}</td>
            <td>${user.major || '-'}</td>
            <td>
                <span class="status-badge ${user.status === 1 ? 'active' : 'inactive'}">
                    ${user.status === 1 ? '活跃' : '禁用'}
                </span>
            </td>
            <td>${formatDate(user.createTime)}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-sm btn-primary" onclick="editUser(${user.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// 更新分页信息
function updatePagination(current, total) {
    const pageInfo = document.getElementById('pageInfo');
    if (pageInfo) {
        pageInfo.textContent = `第 ${current + 1} 页，共 ${total} 页`;
    }
}

// 上一页
function previousPage() {
    if (currentPage > 0) {
        loadUserList(currentPage - 1);
    }
}

// 下一页
function nextPage() {
    if (currentPage < totalPages - 1) {
        loadUserList(currentPage + 1);
    }
}

// 搜索用户
function searchUsers() {
    const searchTerm = document.getElementById('userSearch').value.trim();

    if (!searchTerm) {
        loadUserList(0);
        return;
    }

    // 这里可以实现搜索逻辑，暂时按用户名搜索
    searchUserByUsername(searchTerm);
}

async function searchUserByUsername(username) {
    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/users/username/${username}`);
        const result = await response.json();

        if (result.success) {
            renderUserTable([result.data]);
            updatePagination(0, 1);
        } else {
            showMessage('未找到该用户', 'warning');
            renderUserTable([]);
        }
    } catch (error) {
        console.error('搜索用户错误:', error);
        showMessage('搜索失败，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 编辑用户（占位函数）
function editUser(userId) {
    showMessage('编辑功能开发中', 'info');
}

// 删除用户
async function deleteUser(userId) {
    if (!confirm('确定要删除该用户吗？此操作不可恢复。')) {
        return;
    }

    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
            method: 'DELETE'
        });

        const result = await response.json();

        if (result.success) {
            showMessage('用户删除成功', 'success');
            loadUserList(currentPage);
            loadStatistics();
        } else {
            showMessage(result.message || '删除失败', 'error');
        }
    } catch (error) {
        console.error('删除用户错误:', error);
        showMessage('删除失败，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 刷新用户列表
function refreshUserList() {
    loadUserList(currentPage);
    loadStatistics();
}

// 加载统计数据
async function loadStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/users/stats`);
        const result = await response.json();

        if (result.success) {
            updateStatistics(result.data);
        }
    } catch (error) {
        console.error('加载统计数据错误:', error);
    }
}

// 更新统计显示
function updateStatistics(stats) {
    const totalUsersEl = document.getElementById('totalUsers');
    const activeUsersEl = document.getElementById('activeUsers');

    if (totalUsersEl) {
        totalUsersEl.textContent = stats.totalUsers || 0;
    }

    if (activeUsersEl) {
        activeUsersEl.textContent = stats.activeUsers || 0;
    }

    // 图书数量暂时用占位数据
    const totalBooksEl = document.getElementById('totalBooks');
    if (totalBooksEl) {
        totalBooksEl.textContent = '0';
    }
}

// 显示消息提示
function showMessage(message, type = 'info') {
    const container = document.getElementById('messageContainer');
    if (!container) return;

    const messageEl = document.createElement('div');
    messageEl.className = `message ${type}`;

    let icon = 'fa-info-circle';
    switch (type) {
        case 'success':
            icon = 'fa-check-circle';
            break;
        case 'error':
            icon = 'fa-exclamation-circle';
            break;
        case 'warning':
            icon = 'fa-exclamation-triangle';
            break;
    }

    messageEl.innerHTML = `
        <i class="fas ${icon}"></i>
        <span>${message}</span>
    `;

    container.appendChild(messageEl);

    // 3秒后自动移除
    setTimeout(() => {
        if (messageEl.parentNode) {
            messageEl.parentNode.removeChild(messageEl);
        }
    }, 3000);
}

// 显示加载遮罩
function showLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'flex';
    }
}

// 隐藏加载遮罩
function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '-';

    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// 工具函数：格式化数字
function formatNumber(num) {
    if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'k';
    }
    return num.toString();
}

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 节流函数
function throttle(func, limit) {
    let inThrottle;
    return function() {
        const args = arguments;
        const context = this;
        if (!inThrottle) {
            func.apply(context, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// 模拟数据生成器（用于演示）
function generateMockUsers(count = 5) {
    const mockUsers = [];
    for (let i = 1; i <= count; i++) {
        mockUsers.push({
            id: i,
            username: `user${i}`,
            realName: `用户${i}`,
            email: `user${i}@example.com`,
            studentId: `2021${String(i).padStart(3, '0')}`,
            department: '计算机系',
            major: '软件工程',
            status: 1,
            createTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
            updateTime: new Date().toISOString()
        });
    }
    return mockUsers;
}

// 键盘快捷键
document.addEventListener('keydown', function(e) {
    // Ctrl + R: 刷新当前页面数据
    if (e.ctrlKey && e.key === 'r') {
        e.preventDefault();
        refreshCurrentPage();
    }

    // ESC: 关闭加载遮罩
    if (e.key === 'Escape') {
        hideLoading();
    }
});

// 刷新当前页面数据
function refreshCurrentPage() {
    const activeSection = document.querySelector('.content-section.active');
    if (activeSection) {
        const sectionId = activeSection.id;

        switch (sectionId) {
            case 'dashboard':
                loadStatistics();
                break;
            case 'users':
                loadUserList(currentPage);
                break;
            default:
                break;
        }
    }

    showMessage('数据已刷新', 'success');
}

// 错误处理
window.addEventListener('error', function(e) {
    console.error('页面错误:', e.error);
    showMessage('页面发生错误，请刷新重试', 'error');
});

// 网络状态监听
window.addEventListener('online', function() {
    showMessage('网络连接已恢复', 'success');
});

window.addEventListener('offline', function() {
    showMessage('网络连接已断开', 'warning');
});

// ==================== 图书管理功能 ====================

// 设置图书表单
function setupBookForm() {
    const bookForm = document.getElementById('bookForm');
    if (bookForm) {
        bookForm.addEventListener('submit', function(e) {
            e.preventDefault();
            registerBook();
        });
    }

    // 图书搜索功能
    const bookSearchInput = document.getElementById('bookSearch');
    if (bookSearchInput) {
        bookSearchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                searchBooks();
            }
        });
    }
}

// 注册图书
async function registerBook() {
    showLoading();

    try {
        const formData = new FormData(document.getElementById('bookForm'));
        const bookData = {};

        formData.forEach((value, key) => {
            if (key === 'price') {
                bookData[key] = parseFloat(value) || 0;
            } else if (key === 'totalCopies' || key === 'availableCopies') {
                bookData[key] = parseInt(value) || 1;
            } else {
                bookData[key] = value;
            }
        });

        // 设置默认值
        bookData.status = 1;
        bookData.borrowedCopies = 0;

        const response = await fetch(`${API_BASE_URL}/books`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bookData)
        });

        const result = await response.json();

        if (result.success) {
            showMessage('图书添加成功！', 'success');
            document.getElementById('bookForm').reset();

            // 刷新图书列表和统计数据
            setTimeout(() => {
                loadBookList();
                loadBookStatistics();
            }, 1000);
        } else {
            showMessage(result.message || '添加失败', 'error');
        }
    } catch (error) {
        console.error('添加图书错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 加载图书列表
async function loadBookList(page = 0) {
    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/books?page=${page}&size=${bookPageSize}&sortBy=id&sortDir=desc`);
        const result = await response.json();

        if (result.success) {
            renderBookTable(result.data);
            updateBookPagination(result.currentPage, result.totalPages);
            bookCurrentPage = page;
            bookTotalPages = result.totalPages;
        } else {
            showMessage('加载图书列表失败', 'error');
        }
    } catch (error) {
        console.error('加载图书列表错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 渲染图书表格
function renderBookTable(books) {
    const tableBody = document.getElementById('bookTableBody');

    if (!tableBody) return;

    if (books.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="12" class="text-center">
                    <i class="fas fa-inbox"></i> 暂无图书数据
                </td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = books.map(book => `
        <tr>
            <td>${book.id}</td>
            <td>
                <div class="book-title">
                    ${book.title || '-'}
                    ${book.coverImage ? `<img src="${book.coverImage}" alt="封面" class="book-cover-mini" onerror="this.style.display='none'">` : ''}
                </div>
            </td>
            <td>${book.author || '-'}</td>
            <td>${book.isbn || '-'}</td>
            <td>${book.publisher || '-'}</td>
            <td>¥${book.price || '0.00'}</td>
            <td>${book.category || '-'}</td>
            <td>${book.totalCopies || 0}</td>
            <td>${book.availableCopies || 0}</td>
            <td>${book.borrowedCopies || 0}</td>
            <td>${formatDate(book.createTime)}</td>
            <td>
                <span class="status-badge ${book.status === 1 ? 'active' : 'inactive'}">
                    ${book.status === 1 ? '可借' : '不可借'}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-sm btn-primary" onclick="editBook(${book.id})" title="编辑">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-success" onclick="borrowBook(${book.id})" title="借阅"
                            ${book.availableCopies <= 0 || book.status !== 1 ? 'disabled' : ''}>
                        <i class="fas fa-hand-holding"></i>
                    </button>
                    <button class="btn btn-sm btn-warning" onclick="returnBook(${book.id})" title="归还"
                            ${book.borrowedCopies <= 0 ? 'disabled' : ''}>
                        <i class="fas fa-undo"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteBook(${book.id})" title="删除">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// 更新图书分页信息
function updateBookPagination(current, total) {
    const pageInfo = document.getElementById('bookPageInfo');
    if (pageInfo) {
        pageInfo.textContent = `第 ${current + 1} 页，共 ${total} 页`;
    }
}

// 上一页（图书）
function previousBookPage() {
    if (bookCurrentPage > 0) {
        loadBookList(bookCurrentPage - 1);
    }
}

// 下一页（图书）
function nextBookPage() {
    if (bookCurrentPage < bookTotalPages - 1) {
        loadBookList(bookCurrentPage + 1);
    }
}

// 搜索图书
function searchBooks() {
    const searchTerm = document.getElementById('bookSearch').value.trim();

    if (!searchTerm) {
        loadBookList(0);
        return;
    }

    searchBooksByKeyword(searchTerm);
}

async function searchBooksByKeyword(keyword) {
    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/books/search?keyword=${encodeURIComponent(keyword)}`);
        const result = await response.json();

        if (result.success) {
            renderBookTable(result.data);
            updateBookPagination(0, 1);
        } else {
            showMessage('未找到匹配的图书', 'warning');
            renderBookTable([]);
        }
    } catch (error) {
        console.error('搜索图书错误:', error);
        showMessage('搜索失败，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 编辑图书
function editBook(bookId) {
    showMessage('编辑功能开发中', 'info');
}

// 借阅图书
async function borrowBook(bookId) {
    if (!confirm('确定要借阅这本图书吗？')) {
        return;
    }

    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/books/${bookId}/borrow`, {
            method: 'POST'
        });

        const result = await response.json();

        if (result.success) {
            showMessage('图书借阅成功！', 'success');
            loadBookList(bookCurrentPage);
            loadBookStatistics();
        } else {
            showMessage(result.message || '借阅失败', 'error');
        }
    } catch (error) {
        console.error('借阅图书错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 归还图书
async function returnBook(bookId) {
    if (!confirm('确定要归还这本图书吗？')) {
        return;
    }

    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/books/${bookId}/return`, {
            method: 'POST'
        });

        const result = await response.json();

        if (result.success) {
            showMessage('图书归还成功！', 'success');
            loadBookList(bookCurrentPage);
            loadBookStatistics();
        } else {
            showMessage(result.message || '归还失败', 'error');
        }
    } catch (error) {
        console.error('归还图书错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 删除图书
async function deleteBook(bookId) {
    if (!confirm('确定要删除这本图书吗？此操作不可恢复。')) {
        return;
    }

    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/books/${bookId}`, {
            method: 'DELETE'
        });

        const result = await response.json();

        if (result.success) {
            showMessage('图书删除成功', 'success');
            loadBookList(bookCurrentPage);
            loadBookStatistics();
        } else {
            showMessage(result.message || '删除失败', 'error');
        }
    } catch (error) {
        console.error('删除图书错误:', error);
        showMessage('删除失败，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 刷新图书列表
function refreshBookList() {
    loadBookList(bookCurrentPage);
    loadBookStatistics();
}

// 加载图书统计
async function loadBookStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/books/statistics`);
        const result = await response.json();

        if (result.success) {
            updateBookStatistics(result.data);
        }
    } catch (error) {
        console.error('加载图书统计错误:', error);
    }
}

// 更新图书统计显示
function updateBookStatistics(stats) {
    const totalBooksEl = document.getElementById('totalBooks');
    const availableBooksEl = document.getElementById('availableBooks');
    const borrowedBooksEl = document.getElementById('borrowedBooks');

    if (totalBooksEl) {
        totalBooksEl.textContent = stats.totalBooks || 0;
    }

    if (availableBooksEl) {
        availableBooksEl.textContent = stats.availableBooks || 0;
    }

    if (borrowedBooksEl) {
        borrowedBooksEl.textContent = stats.borrowedBooks || 0;
    }

    // 更新用户数量（之前已经设置）
    const totalUsersEl = document.getElementById('totalUsers');
    if (totalUsersEl && totalUsersEl.textContent === '0') {
        loadStatistics(); // 如果用户统计还没有加载，重新加载
    }
}

// 显示添加图书表单
function showAddBook() {
    // 切换到图书管理页面
    document.querySelector('[href="#books"]').click();

    // 显示添加表单
    const registrationDiv = document.getElementById('bookRegistration');
    const listDiv = document.getElementById('bookList');

    if (registrationDiv) {
        registrationDiv.style.display = 'block';
        registrationDiv.scrollIntoView({ behavior: 'smooth' });
    }

    if (listDiv) {
        listDiv.style.display = 'none';
    }
}

// 显示图书列表
function showBookList() {
    // 切换到图书管理页面
    document.querySelector('[href="#books"]').click();

    // 显示图书列表
    const registrationDiv = document.getElementById('bookRegistration');
    const listDiv = document.getElementById('bookList');

    if (registrationDiv) {
        registrationDiv.style.display = 'none';
    }

    if (listDiv) {
        listDiv.style.display = 'block';
        loadBookList(); // 重新加载图书列表
    }
}

// ==================== 借阅记录功能 ====================

// 借阅记录分页变量
let borrowCurrentPage = 0;
let borrowPageSize = 10;
let borrowTotalPages = 0;

// 显示借阅表单
function showBorrowForm() {
    // 切换到借阅记录页面
    document.querySelector('[href="#records"]').click();

    // 显示借阅表单
    const formDiv = document.getElementById('borrowForm');
    const listDiv = document.getElementById('borrowRecordsList');

    if (formDiv) {
        formDiv.style.display = 'block';
        formDiv.scrollIntoView({ behavior: 'smooth' });
    }

    if (listDiv) {
        listDiv.style.display = 'none';
    }

    // 设置表单提交事件
    setupBorrowForm();
}

// 隐藏借阅表单
function hideBorrowForm() {
    const formDiv = document.getElementById('borrowForm');
    const listDiv = document.getElementById('borrowRecordsList');

    if (formDiv) {
        formDiv.style.display = 'none';
    }

    if (listDiv) {
        listDiv.style.display = 'block';
    }

    // 清空表单
    document.getElementById('borrowBookForm').reset();
}

// 设置借阅表单
function setupBorrowForm() {
    const borrowForm = document.getElementById('borrowBookForm');
    if (borrowForm) {
        borrowForm.addEventListener('submit', function(e) {
            e.preventDefault();
            processBorrow();
        });
    }
}

// 处理借阅
async function processBorrow() {
    showLoading();

    try {
        const formData = new FormData(document.getElementById('borrowBookForm'));
        const userId = formData.get('userId');
        const bookId = formData.get('bookId');

        const response = await fetch(`${API_BASE_URL}/borrow-records/borrow?userId=${userId}&bookId=${bookId}`, {
            method: 'POST'
        });

        const result = await response.json();

        if (result.success) {
            showMessage('图书借阅成功！', 'success');
            hideBorrowForm();
            refreshBorrowRecords();
        } else {
            showMessage(result.message || '借阅失败', 'error');
        }
    } catch (error) {
        console.error('借阅图书错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 加载借阅记录
async function loadBorrowRecords(page = 0, status = '') {
    showLoading();

    try {
        let url = `${API_BASE_URL}/borrow-records?page=${page}&size=${borrowPageSize}`;
        if (status) {
            url = `${API_BASE_URL}/borrow-records/status/${status}?page=${page}&size=${borrowPageSize}`;
        }

        const response = await fetch(url);
        const result = await response.json();

        if (result.success) {
            renderBorrowRecordsTable(result.data);
            updateBorrowPagination(result.currentPage, result.totalPages);
            borrowCurrentPage = page;
            borrowTotalPages = result.totalPages;
        } else {
            showMessage('加载借阅记录失败', 'error');
        }
    } catch (error) {
        console.error('加载借阅记录错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 渲染借阅记录表格
function renderBorrowRecordsTable(records) {
    const tableBody = document.getElementById('borrowRecordsTableBody');

    if (!tableBody) return;

    if (records.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="10" class="text-center">
                    <i class="fas fa-inbox"></i> 暂无借阅记录
                </td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = records.map(record => `
        <tr>
            <td>${record.id}</td>
            <td>
                <div class="user-info">
                    <strong>${record.user ? record.user.username : 'N/A'}</strong><br>
                    <small>${record.user ? record.user.realName || '-' : ''}</small>
                </div>
            </td>
            <td>
                <div class="book-info">
                    <strong>${record.book ? record.book.title : 'N/A'}</strong><br>
                    <small>${record.book ? record.book.author || '-' : ''}</small>
                </div>
            </td>
            <td>${formatDate(record.borrowDate)}</td>
            <td>${formatDate(record.dueDate)}</td>
            <td>${record.returnDate ? formatDate(record.returnDate) : '-'}</td>
            <td>
                <span class="status-badge ${getBorrowStatusClass(record.status)}">
                    ${getBorrowStatusText(record.status)}
                </span>
            </td>
            <td>${record.renewCount || 0}</td>
            <td>￥${record.fineAmount || '0.00'}</td>
            <td>
                <div class="action-buttons">
                    ${record.status === 1 ? `
                        <button class="btn btn-sm btn-success" onclick="returnBook(${record.id})" title="归还">
                            <i class="fas fa-undo"></i>
                        </button>
                        <button class="btn btn-sm btn-info" onclick="renewBook(${record.id})" title="续借">
                            <i class="fas fa-redo"></i>
                        </button>
                    ` : ''}
                    <button class="btn btn-sm btn-primary" onclick="viewBorrowRecord(${record.id})" title="详情">
                        <i class="fas fa-eye"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// 获取借阅状态样式类
function getBorrowStatusClass(status) {
    switch (status) {
        case 1: return 'borrowing'; // 借阅中
        case 2: return 'returned';  // 已归还
        case 3: return 'overdue';   // 逾期
        default: return '';
    }
}

// 获取借阅状态文本
function getBorrowStatusText(status) {
    switch (status) {
        case 1: return '借阅中';
        case 2: return '已归还';
        case 3: return '逾期';
        default: return '未知';
    }
}

// 更新借阅记录分页信息
function updateBorrowPagination(current, total) {
    const pageInfo = document.getElementById('borrowPageInfo');
    const prevBtn = document.getElementById('previousBorrowBtn');
    const nextBtn = document.getElementById('nextBorrowBtn');

    if (pageInfo) {
        pageInfo.textContent = `第 ${current + 1} 页，共 ${total} 页`;
    }

    if (prevBtn) {
        prevBtn.disabled = current <= 0;
    }

    if (nextBtn) {
        nextBtn.disabled = current >= total - 1;
    }
}

// 上一页（借阅记录）
function previousBorrowPage() {
    const statusFilter = document.getElementById('recordStatusFilter').value;
    if (borrowCurrentPage > 0) {
        loadBorrowRecords(borrowCurrentPage - 1, statusFilter);
    }
}

// 下一页（借阅记录）
function nextBorrowPage() {
    const statusFilter = document.getElementById('recordStatusFilter').value;
    if (borrowCurrentPage < borrowTotalPages - 1) {
        loadBorrowRecords(borrowCurrentPage + 1, statusFilter);
    }
}

// 搜索借阅记录
function searchBorrowRecords() {
    const searchTerm = document.getElementById('recordSearch').value.trim();
    const statusFilter = document.getElementById('recordStatusFilter').value;

    if (!searchTerm && !statusFilter) {
        loadBorrowRecords(0);
        return;
    }

    searchBorrowRecordsByKeyword(searchTerm, statusFilter);
}

async function searchBorrowRecordsByKeyword(keyword, status) {
    showLoading();

    try {
        let url = `${API_BASE_URL}/borrow-records/search?page=0&size=${borrowPageSize}`;
        if (keyword) {
            url += `&username=${encodeURIComponent(keyword)}&bookTitle=${encodeURIComponent(keyword)}`;
        }

        const response = await fetch(url);
        const result = await response.json();

        if (result.success) {
            renderBorrowRecordsTable(result.data);
            updateBorrowPagination(0, 1);
        } else {
            showMessage('未找到匹配的借阅记录', 'warning');
            renderBorrowRecordsTable([]);
        }
    } catch (error) {
        console.error('搜索借阅记录错误:', error);
        showMessage('搜索失败，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 加载逾期记录
async function loadOverdueRecords() {
    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/borrow-records/overdue?page=0&size=${borrowPageSize}`);
        const result = await response.json();

        if (result.success) {
            renderBorrowRecordsTable(result.data);
            updateBorrowPagination(0, result.totalPages);
            showMessage(`找到 ${result.data.length} 条逾期记录`, 'warning');
        } else {
            showMessage('加载逾期记录失败', 'error');
        }
    } catch (error) {
        console.error('加载逾期记录错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 加载即将到期记录
async function loadDueSoonRecords() {
    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/borrow-records/due-soon`);
        const result = await response.json();

        if (result.success) {
            renderBorrowRecordsTable(result.data);
            updateBorrowPagination(0, 1);
            showMessage(`找到 ${result.data.length} 条即将到期记录`, 'info');
        } else {
            showMessage('加载即将到期记录失败', 'error');
        }
    } catch (error) {
        console.error('加载即将到期记录错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 归还图书
async function returnBook(recordId) {
    if (!confirm('确定要归还这本图书吗？')) {
        return;
    }

    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/borrow-records/${recordId}/return`, {
            method: 'POST'
        });

        const result = await response.json();

        if (result.success) {
            showMessage('图书归还成功！', 'success');
            refreshBorrowRecords();
        } else {
            showMessage(result.message || '归还失败', 'error');
        }
    } catch (error) {
        console.error('归还图书错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 续借图书
async function renewBook(recordId) {
    if (!confirm('确定要续借这本图书吗？')) {
        return;
    }

    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/borrow-records/${recordId}/renew`, {
            method: 'POST'
        });

        const result = await response.json();

        if (result.success) {
            showMessage('图书续借成功！', 'success');
            refreshBorrowRecords();
        } else {
            showMessage(result.message || '续借失败', 'error');
        }
    } catch (error) {
        console.error('续借图书错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 查看借阅记录详情
async function viewBorrowRecord(recordId) {
    showLoading();

    try {
        const response = await fetch(`${API_BASE_URL}/borrow-records/${recordId}`);
        const result = await response.json();

        if (result.success) {
            const record = result.data;
            showBorrowRecordDetails(record);
        } else {
            showMessage('获取借阅记录详情失败', 'error');
        }
    } catch (error) {
        console.error('获取借阅记录详情错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    } finally {
        hideLoading();
    }
}

// 显示借阅记录详情
function showBorrowRecordDetails(record) {
    let detailsHTML = `
        <div style="max-width: 500px; margin: 0 auto;">
            <h3>借阅记录详情</h3>
            <p><strong>记录ID:</strong> ${record.id}</p>
            <p><strong>用户:</strong> ${record.user ? record.user.username : 'N/A'} (${record.user ? record.user.realName : ''})</p>
            <p><strong>图书:</strong> ${record.book ? record.book.title : 'N/A'}</p>
            <p><strong>作者:</strong> ${record.book ? record.book.author : 'N/A'}</p>
            <p><strong>借阅日期:</strong> ${formatDate(record.borrowDate)}</p>
            <p><strong>应还日期:</strong> ${formatDate(record.dueDate)}</p>
            <p><strong>归还日期:</strong> ${record.returnDate ? formatDate(record.returnDate) : '未归还'}</p>
            <p><strong>状态:</strong> <span class="status-badge ${getBorrowStatusClass(record.status)}">${getBorrowStatusText(record.status)}</span></p>
            <p><strong>续借次数:</strong> ${record.renewCount || 0}</p>
            <p><strong>罚金:</strong> ￥${record.fineAmount || '0.00'}</p>
            ${record.notes ? `<p><strong>备注:</strong> ${record.notes}</p>` : ''}
        </div>
    `;

    // 简单的模态框显示
    const modalOverlay = document.createElement('div');
    modalOverlay.className = 'modal-overlay';
    modalOverlay.innerHTML = `
        <div class="modal-content">
            ${detailsHTML}
            <div class="modal-actions">
                <button class="btn btn-primary" onclick="this.closest('.modal-overlay').remove()">确定</button>
            </div>
        </div>
    `;

    document.body.appendChild(modalOverlay);
}

// 刷新借阅记录
function refreshBorrowRecords() {
    const statusFilter = document.getElementById('recordStatusFilter').value;
    loadBorrowRecords(0, statusFilter);
}

// 状态筛选器变化事件监听
document.addEventListener('DOMContentLoaded', function() {
    const statusFilter = document.getElementById('recordStatusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', function() {
            const status = this.value;
            loadBorrowRecords(0, status);
        });
    }

    // 搜索框回车事件
    const recordSearch = document.getElementById('recordSearch');
    if (recordSearch) {
        recordSearch.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchBorrowRecords();
            }
        });
    }
});

// ==================== 管理员功能 ====================

// 显示管理员统计
function showAdminStats() {
    showLoading();
    apiRequest('/dashboard/overview')
        .then(data => {
            if (data.success) {
                displayAdminStats(data.data);
            }
        })
        .catch(error => {
            showMessage('加载统计数据失败', 'error');
        })
        .finally(() => {
            hideLoading();
        });
}

// 显示管理员统计数据
function displayAdminStats(stats) {
    const statsGrid = document.getElementById('adminStatsGrid');
    if (!statsGrid) return;

    statsGrid.innerHTML = `
        <div class="stat-card">
            <div class="stat-icon blue">
                <i class="fas fa-users"></i>
            </div>
            <div class="stat-content">
                <h3>${stats.totalUsers}</h3>
                <p>总用户数</p>
                <small>活跃: ${stats.activeUsers}</small>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon green">
                <i class="fas fa-book"></i>
            </div>
            <div class="stat-content">
                <h3>${stats.totalBooks}</h3>
                <p>总图书数</p>
                <small>可借: ${stats.availableBooks}</small>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon orange">
                <i class="fas fa-hand-holding"></i>
            </div>
            <div class="stat-content">
                <h3>${stats.activeBorrows}</h3>
                <p>活跃借阅</p>
                <small>逾期: ${stats.overdueBorrows}</small>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon purple">
                <i class="fas fa-heart"></i>
            </div>
            <div class="stat-content">
                <h3>${stats.totalFavorites}</h3>
                <p>总收藏数</p>
                <small>图书: ${stats.favoritedBooks}</small>
            </div>
        </div>
    `;
}

// 刷新管理员数据
function refreshAdminData() {
    showAdminStats();
    loadSystemHealth();
}

// 显示创建管理员表单
function showCreateAdmin() {
    const formHtml = `
        <div class="modal" style="display: block;">
            <div class="modal-content">
                <div class="modal-header">
                    <h3><i class="fas fa-user-plus"></i> 创建管理员</h3>
                </div>
                <div class="modal-body">
                    <form id="createAdminForm">
                        <div class="form-group">
                            <label for="adminUsername">用户名 *</label>
                            <input type="text" id="adminUsername" name="username" required>
                        </div>
                        <div class="form-group">
                            <label for="adminPassword">密码 *</label>
                            <input type="password" id="adminPassword" name="password" required>
                        </div>
                        <div class="form-group">
                            <label for="adminEmail">邮箱</label>
                            <input type="email" id="adminEmail" name="email">
                        </div>
                        <div class="form-group">
                            <label for="adminRealName">真实姓名</label>
                            <input type="text" id="adminRealName" name="realName">
                        </div>
                        <div class="form-group">
                            <label for="adminRole">角色</label>
                            <select id="adminRole" name="role">
                                <option value="LIBRARIAN">图书管理员</option>
                                <option value="ADMIN">系统管理员</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="adminDepartment">部门</label>
                            <input type="text" id="adminDepartment" name="department">
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">创建</button>
                            <button type="button" class="btn btn-secondary" onclick="closeModal()">取消</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', formHtml);

    document.getElementById('createAdminForm').addEventListener('submit', function(e) {
        e.preventDefault();
        createAdmin();
    });
}

// 创建管理员
function createAdmin() {
    const formData = {
        username: document.getElementById('adminUsername').value,
        password: document.getElementById('adminPassword').value,
        email: document.getElementById('adminEmail').value,
        realName: document.getElementById('adminRealName').value,
        role: document.getElementById('adminRole').value,
        department: document.getElementById('adminDepartment').value
    };

    apiRequest('/admin', {
        method: 'POST',
        body: JSON.stringify(formData)
    })
        .then(data => {
            if (data.success) {
                showMessage('管理员创建成功', 'success');
                closeModal();
            } else {
                showMessage(data.message || '创建失败', 'error');
            }
        })
        .catch(error => {
            showMessage('创建失败', 'error');
        });
}

// 显示管理员列表
function showAdminList() {
    // 这里可以实现管理员列表的显示逻辑
    showMessage('管理员列表功能开发中...', 'info');
}

// 显示禁用用户
function showDisabledUsers() {
    // 这里可以实现禁用用户的显示逻辑
    showMessage('禁用用户功能开发中...', 'info');
}

// 导出用户数据
function exportUserData() {
    // 这里可以实现用户数据导出功能
    showMessage('用户数据导出功能开发中...', 'info');
}

// 显示图书统计
function showBookStats() {
    // 这里可以实现图书统计的显示逻辑
    showMessage('图书统计功能开发中...', 'info');
}

// 显示分类管理
function showCategoryManagement() {
    // 这里可以实现分类管理的显示逻辑
    showMessage('分类管理功能开发中...', 'info');
}

// 显示逾期记录
function showOverdueRecords() {
    showLoading();
    apiRequest('/dashboard/overdue')
        .then(data => {
            if (data.success) {
                displayOverdueRecords(data.data);
            }
        })
        .catch(error => {
            showMessage('加载逾期记录失败', 'error');
        })
        .finally(() => {
            hideLoading();
        });
}

// 显示借阅统计
function showBorrowStatistics() {
    // 这里可以实现借阅统计的显示逻辑
    showMessage('借阅统计功能开发中...', 'info');
}

// 加载系统健康状态
function loadSystemHealth() {
    apiRequest('/dashboard/health')
        .then(data => {
            if (data.success) {
                displaySystemHealth(data.data);
            }
        })
        .catch(error => {
            console.error('加载系统健康状态失败:', error);
        });
}

// 显示系统健康状态
function displaySystemHealth(health) {
    document.getElementById('dbStatus').textContent = health.database === 'healthy' ? '正常' : '异常';
    document.getElementById('activeConnections').textContent = health.activeConnections || 0;
    document.getElementById('systemLoad').textContent = '正常';
    document.getElementById('diskUsage').textContent = health.diskUsage || '45%';
}

// 关闭模态框
function closeModal() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (modal.style.display === 'block' && modal.id !== 'loginModal' && modal.id !== 'registerModal') {
            modal.remove();
        }
    });
}