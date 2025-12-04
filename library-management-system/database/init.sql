-- 图书管理系统数据库初始化脚本
-- 请先创建数据库：CREATE DATABASE `library_management_system` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_management_system`;

-- 1. 普通用户表
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `student_id` VARCHAR(20) COMMENT '学号',
  `department` VARCHAR(100) COMMENT '院系',
  `major` VARCHAR(100) COMMENT '专业',
  `grade` VARCHAR(20) COMMENT '年级',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:正常)',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='普通用户表';

-- 2. 管理员表
CREATE TABLE `admins` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) NOT NULL COMMENT '管理员用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `role` VARCHAR(20) DEFAULT 'admin' COMMENT '角色(admin:超级管理员 librarian:图书管理员)',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:正常)',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 3. 图书分类表
CREATE TABLE `book_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `category_code` VARCHAR(20) NOT NULL COMMENT '分类编码',
  `description` VARCHAR(500) COMMENT '分类描述',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID(0:顶级分类)',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 4. 图书信息表
CREATE TABLE `books` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `isbn` VARCHAR(20) COMMENT 'ISBN号',
  `title` VARCHAR(200) NOT NULL COMMENT '书名',
  `author` VARCHAR(100) NOT NULL COMMENT '作者',
  `publisher` VARCHAR(100) COMMENT '出版社',
  `publish_date` DATE COMMENT '出版日期',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `description` TEXT COMMENT '图书描述',
  `cover_image` VARCHAR(255) COMMENT '封面图片URL',
  `page_count` INT COMMENT '页数',
  `language` VARCHAR(20) DEFAULT '中文' COMMENT '语言',
  `price` DECIMAL(10,2) COMMENT '价格',
  `total_copies` INT DEFAULT 0 COMMENT '总副本数',
  `available_copies` INT DEFAULT 0 COMMENT '可借副本数',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:下架 1:上架)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_isbn` (`isbn`),
  KEY `idx_title` (`title`),
  KEY `idx_author` (`author`),
  KEY `idx_category_id` (`category_id`),
  FOREIGN KEY (`category_id`) REFERENCES `book_categories` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';

-- 5. 图书记录表(图书副本)
CREATE TABLE `book_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `barcode` VARCHAR(50) NOT NULL COMMENT '图书条码',
  `location` VARCHAR(100) COMMENT '馆藏位置',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:借出 1:可借 2:维护 3:遗失)',
  `purchase_date` DATE COMMENT '采购日期',
  `price` DECIMAL(10,2) COMMENT '采购价格',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_barcode` (`barcode`),
  KEY `idx_book_id` (`book_id`),
  FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书记录表';

-- 6. 借阅记录表
CREATE TABLE `borrow_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '借阅记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `book_record_id` BIGINT NOT NULL COMMENT '图书记录ID',
  `admin_id` BIGINT COMMENT '处理管理员ID',
  `borrow_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借阅日期',
  `due_date` DATETIME NOT NULL COMMENT '应还日期',
  `return_date` DATETIME COMMENT '实际归还日期',
  `renew_count` INT DEFAULT 0 COMMENT '续借次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0:已取消 1:借阅中 2:已归还 3:逾期)',
  `fine_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '罚金金额',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_book_record_id` (`book_record_id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_borrow_date` (`borrow_date`),
  KEY `idx_status` (`status`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`book_record_id`) REFERENCES `book_records` (`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`admin_id`) REFERENCES `admins` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 7. 用户收藏表
CREATE TABLE `favorites` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 初始化管理员数据 (密码: admin123)
INSERT INTO `admins` (`username`, `password`, `real_name`, `email`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC', '超级管理员', 'admin@library.com', 'admin'),
('librarian', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC', '图书管理员', 'librarian@library.com', 'librarian');

-- 初始化图书分类数据
INSERT INTO `book_categories` (`category_name`, `category_code`, `description`, `sort_order`) VALUES
('文学', 'LITERATURE', '小说、散文、诗歌等文学作品', 1),
('科技', 'TECHNOLOGY', '计算机、工程、科学技术类图书', 2),
('历史', 'HISTORY', '历史、传记类图书', 3),
('艺术', 'ART', '美术、音乐、艺术设计类图书', 4),
('哲学', 'PHILOSOPHY', '哲学、心理学类图书', 5),
('经济', 'ECONOMICS', '经济学、管理学类图书', 6),
('教育', 'EDUCATION', '教育、教学类图书', 7);

-- 初始化测试图书数据
INSERT INTO `books` (`title`, `author`, `publisher`, `publish_date`, `category_id`, `description`, `total_copies`, `available_copies`) VALUES
('Java核心技术卷I', 'Cay S. Horstmann', '机械工业出版社', '2018-09-01', 2, 'Java技术经典参考书', 10, 10),
('三国演义', '罗贯中', '人民文学出版社', '2019-01-01', 1, '中国古典四大名著之一', 5, 5),
('人类简史', '尤瓦尔·赫拉利', '中信出版社', '2017-02-01', 6, '从石器时代到21世纪的人类发展史', 8, 8),
('红楼梦', '曹雪芹', '人民文学出版社', '2018-07-01', 1, '中国古典四大名著之一', 6, 6),
('算法导论', 'Thomas H. Cormen', '机械工业出版社', '2019-11-01', 2, '计算机算法经典教材', 12, 12);