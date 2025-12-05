-- 图书管理系统数据库初始化脚本
-- 符合第三范式设计，确保数据无冗余

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_management_system
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_management_system;

-- 1. 用户表 (users)
-- 存储普通用户信息，符合第三范式
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) COMMENT '真实姓名',
    student_id VARCHAR(20) COMMENT '学号',
    department VARCHAR(100) COMMENT '院系',
    major VARCHAR(100) COMMENT '专业',
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER' COMMENT '用户角色',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-激活，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status),
    INDEX idx_role (role)
) ENGINE=InnoDB COMMENT='用户表';

-- 2. 管理员表 (admins)
-- 存储管理员信息，与用户表分离，符合第三范式
CREATE TABLE admins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) COMMENT '真实姓名',
    employee_id VARCHAR(20) COMMENT '员工号',
    role ENUM('ADMIN', 'LIBRARIAN') NOT NULL DEFAULT 'LIBRARIAN' COMMENT '管理员角色',
    department VARCHAR(200) COMMENT '部门',
    status BOOLEAN NOT NULL DEFAULT TRUE COMMENT '状态：TRUE-激活，FALSE-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_admin_username (username),
    INDEX idx_admin_email (email),
    INDEX idx_employee_id (employee_id),
    INDEX idx_admin_status (status),
    INDEX idx_admin_role (role)
) ENGINE=InnoDB COMMENT='管理员表';

-- 3. 图书分类表 (book_categories)
-- 图书分类信息，支持层级结构，符合第三范式
CREATE TABLE book_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(20) COMMENT '分类代码',
    description TEXT COMMENT '分类描述',
    parent_id BIGINT COMMENT '父级分类ID',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    status BOOLEAN NOT NULL DEFAULT TRUE COMMENT '状态：TRUE-启用，FALSE-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (parent_id) REFERENCES book_categories(id) ON DELETE SET NULL,
    INDEX idx_category_name (category_name),
    INDEX idx_code (code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='图书分类表';

-- 4. 图书表 (books)
-- 图书基本信息，符合第三范式
CREATE TABLE books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    title VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) NOT NULL COMMENT '作者',
    isbn VARCHAR(20) NOT NULL COMMENT 'ISBN',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATETIME COMMENT '出版日期',
    price DECIMAL(10,2) COMMENT '价格',
    category_id BIGINT COMMENT '分类ID',
    description TEXT COMMENT '描述',
    cover_image VARCHAR(500) COMMENT '封面图片URL',
    total_copies INT NOT NULL DEFAULT 1 COMMENT '总数量',
    available_copies INT NOT NULL DEFAULT 1 COMMENT '可借阅数量',
    borrowed_copies INT NOT NULL DEFAULT 0 COMMENT '已借出数量',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-可用，0-不可用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (category_id) REFERENCES book_categories(id) ON DELETE SET NULL,
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_isbn (isbn),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_available_copies (available_copies),
    CONSTRAINT chk_copies CHECK (total_copies >= available_copies AND available_copies >= borrowed_copies)
) ENGINE=InnoDB COMMENT='图书表';

-- 5. 借阅记录表 (borrow_records)
-- 用户借阅记录，符合第三范式
CREATE TABLE borrow_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '借阅记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅日期',
    due_date DATETIME NOT NULL COMMENT '应还日期',
    return_date DATETIME COMMENT '归还日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-借阅中，2-已归还，3-逾期',
    renew_count INT NOT NULL DEFAULT 0 COMMENT '续借次数',
    fine DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '罚金',
    remarks TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    INDEX idx_due_date (due_date),
    INDEX idx_return_date (return_date)
) ENGINE=InnoDB COMMENT='借阅记录表';

-- 6. 图书记录表 (book_records)
-- 图书副本记录，符合第三范式
CREATE TABLE book_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    record_number VARCHAR(50) NOT NULL COMMENT '记录编号',
    location VARCHAR(100) COMMENT '存放位置',
    condition_status VARCHAR(50) DEFAULT '良好' COMMENT '状态：良好、损坏、维修中',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-可借，0-不可借',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_book_id (book_id),
    INDEX idx_record_number (record_number),
    INDEX idx_status (status),
    UNIQUE KEY uk_record_number (record_number)
) ENGINE=InnoDB COMMENT='图书记录表';

-- 7. 用户收藏表 (favorites)
-- 用户收藏图书，符合第三范式
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    UNIQUE KEY uk_user_book (user_id, book_id)
) ENGINE=InnoDB COMMENT='用户收藏表';

-- 插入初始数据

-- 插入默认管理员账户 (密码: admin123, 已使用BCrypt加密)
INSERT INTO admins (username, password, email, real_name, role, status) VALUES
('admin', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'admin@library.com', '系统管理员', 'ADMIN', TRUE),
('librarian', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'librarian@library.com', '图书管理员', 'LIBRARIAN', TRUE);

-- 插入默认图书分类
INSERT INTO book_categories (category_name, code, description, sort_order) VALUES
('计算机', 'CS', '计算机科学与技术相关书籍', 1),
('文学', 'LIT', '文学作品类书籍', 2),
('历史', 'HIS', '历史类书籍', 3),
('科学', 'SCI', '自然科学类书籍', 4),
('艺术', 'ART', '艺术类书籍', 5),
('哲学', 'PHI', '哲学类书籍', 6),
('经济', 'ECO', '经济学类书籍', 7),
('教育', 'EDU', '教育类书籍', 8),
('其他', 'OTH', '其他类别书籍', 9);

-- 插入示例图书数据
INSERT INTO books (title, author, isbn, publisher, price, category_id, total_copies, available_copies, description) VALUES
('Java编程思想', 'Bruce Eckel', '9787111213826', '机械工业出版社', 108.00, 1, 10, 8, 'Java编程经典教程'),
('Spring实战', 'Craig Walls', '9787115417305', '人民邮电出版社', 99.00, 1, 5, 4, 'Spring框架实战指南'),
('红楼梦', '曹雪芹', '9787020002207', '人民文学出版社', 59.70, 2, 8, 6, '中国古典文学四大名著之一'),
('活着', '余华', '9787020024759', '作家出版社', 20.00, 2, 10, 9, '余华代表作'),
('人类简史', '尤瓦尔·赫拉利', '9787508647357', '中信出版社', 68.00, 3, 6, 5, '从动物到上帝的人类发展史');

-- 插入示例用户数据 (密码: 123456, 已使用BCrypt加密)
INSERT INTO users (username, password, email, real_name, student_id, department, major, role, status) VALUES
('student1', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'student1@campus.edu', '张三', '2021001', '计算机学院', '软件工程', 'USER', 1),
('student2', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'student2@campus.edu', '李四', '2021002', '文学院', '汉语言文学', 'USER', 1);

-- 插入示例借阅记录
INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, status) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), 1),
(2, 3, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 1);

-- 插入图书记录（图书副本）
INSERT INTO book_records (book_id, record_number, location, condition_status) VALUES
(1, 'BOOK001-001', 'A区-1层-001', '良好'),
(1, 'BOOK001-002', 'A区-1层-002', '良好'),
(1, 'BOOK001-003', 'A区-1层-003', '良好'),
(2, 'BOOK002-001', 'A区-2层-001', '良好'),
(2, 'BOOK002-002', 'A区-2层-002', '良好'),
(3, 'BOOK003-001', 'B区-1层-001', '良好'),
(3, 'BOOK003-002', 'B区-1层-002', '良好'),
(4, 'BOOK004-001', 'B区-2层-001', '良好');

COMMIT;