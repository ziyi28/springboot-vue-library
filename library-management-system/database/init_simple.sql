-- 图书管理系统数据库初始化脚本 (简化版)

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `library_management_system`
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `library_management_system`;

-- 1. 管理员表
CREATE TABLE `admins` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `real_name` VARCHAR(50),
  `role` VARCHAR(20) DEFAULT 'admin',
  `status` TINYINT DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 用户表
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) UNIQUE,
  `real_name` VARCHAR(50),
  `student_id` VARCHAR(20) UNIQUE,
  `status` TINYINT DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 图书分类表
CREATE TABLE `book_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(100) NOT NULL,
  `category_code` VARCHAR(20) NOT NULL UNIQUE,
  `description` VARCHAR(500),
  `status` TINYINT DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 图书表
CREATE TABLE `books` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `isbn` VARCHAR(20) UNIQUE,
  `title` VARCHAR(200) NOT NULL,
  `author` VARCHAR(100) NOT NULL,
  `publisher` VARCHAR(100),
  `category_id` BIGINT NOT NULL,
  `description` TEXT,
  `total_copies` INT DEFAULT 0,
  `available_copies` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 借阅记录表
CREATE TABLE `borrow_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `book_id` BIGINT NOT NULL,
  `borrow_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `due_date` DATETIME NOT NULL,
  `return_date` DATETIME,
  `renew_count` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入初始数据
INSERT INTO `admins` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC', '超级管理员', 'admin'),
('librarian', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC', '图书管理员', 'librarian');

INSERT INTO `book_categories` (`category_name`, `category_code`, `description`) VALUES
('文学', 'LITERATURE', '小说、散文、诗歌等文学作品'),
('科技', 'TECHNOLOGY', '计算机、工程、科学技术类图书'),
('历史', 'HISTORY', '历史、传记类图书'),
('艺术', 'ART', '美术、音乐、艺术设计类图书'),
('哲学', 'PHILOSOPHY', '哲学、心理学类图书');

INSERT INTO `books` (`title`, `author`, `publisher`, `category_id`, `description`, `total_copies`, `available_copies`) VALUES
('Java核心技术卷I', 'Cay S. Horstmann', '机械工业出版社', 2, 'Java技术经典参考书', 10, 10),
('三国演义', '罗贯中', '人民文学出版社', 1, '中国古典四大名著之一', 5, 5),
('人类简史', '尤瓦尔·赫拉利', '中信出版社', 5, '从石器时代到21世纪的人类发展史', 8, 8),
('红楼梦', '曹雪芹', '人民文学出版社', 1, '中国古典四大名著之一', 6, 6),
('算法导论', 'Thomas H. Cormen', '机械工业出版社', 2, '计算机算法经典教材', 12, 12);

-- 显示创建结果
SHOW TABLES;
SELECT 'Database initialized successfully' as message;
SELECT COUNT(*) as admin_count FROM admins;
SELECT COUNT(*) as book_count FROM books;