// 全局变量
let currentPage = 0;
let pageSize = 10;
let totalPages = 0;

// API基础URL
const API_BASE_URL = '/api';

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

// 初始化应用
function initializeApp() {
    // 导航菜单事件监听
    setupNavigation();

    // 用户表单提交事件
    setupUserForm();

    // 加载统计数据
    loadStatistics();

    // 加载用户列表
    loadUserList();

    // 设置搜索功能
    setupSearch();
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