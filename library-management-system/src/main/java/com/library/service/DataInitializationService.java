package com.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.model.*;
import com.library.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数据初始化服务
 * 系统启动时自动创建初始数据
 */
// @Service  // 暂时禁用
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 检查是否需要初始化数据
        if (needsInitialization()) {
            initializeData();
            System.out.println("📚 图书管理系统数据初始化完成！");
        } else {
            System.out.println("📚 数据库已包含数据，跳过初始化。");
        }
    }

    private boolean needsInitialization() {
        return adminRepository.count() == 0;
    }

    @Transactional
    private void initializeData() {
        System.out.println("🔄 开始初始化图书管理系统数据...");

        // 1. 初始化管理员账户
        initializeAdmins();

        // 2. 初始化图书分类
        initializeBookCategories();

        // 3. 初始化示例图书
        initializeBooks();

        // 4. 初始化示例用户
        initializeUsers();

        System.out.println("✅ 数据初始化完成！");
    }

    private void initializeAdmins() {
        System.out.println("👨‍💼 创建管理员账户...");

        // 创建系统管理员
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@library.com");
        admin.setRealName("系统管理员");
        admin.setRole(Admin.AdminRole.ADMIN);
        admin.setStatus(true);
        adminRepository.save(admin);

        // 创建图书管理员
        Admin librarian = new Admin();
        librarian.setUsername("librarian");
        librarian.setPassword(passwordEncoder.encode("admin123"));
        librarian.setEmail("librarian@library.com");
        librarian.setRealName("图书管理员");
        librarian.setRole(Admin.AdminRole.LIBRARIAN);
        librarian.setDepartment("图书馆");
        librarian.setStatus(true);
        adminRepository.save(librarian);

        System.out.println("  ✅ 管理员账户创建完成");
    }

    private void initializeBookCategories() {
        System.out.println("📂 创建图书分类...");

        String[] categories = {
            "计算机", "文学", "历史", "科学", "艺术", "哲学", "经济", "教育", "其他"
        };

        for (String categoryName : categories) {
            BookCategory category = new BookCategory();
            category.setCategoryName(categoryName);
            category.setCode(categoryName.substring(0, Math.min(3, categoryName.length())).toUpperCase());
            category.setDescription(categoryName + "类图书");
            category.setStatus(true);
            bookCategoryRepository.save(category);
        }

        System.out.println("  ✅ 图书分类创建完成");
    }

    private void initializeBooks() {
        System.out.println("📚 创建示例图书...");

        // 获取第一个分类（计算机类）
        BookCategory computerCategory = bookCategoryRepository.findAll().get(0);
        BookCategory literatureCategory = bookCategoryRepository.findAll().get(1);

        // 创建示例图书
        Book book1 = new Book();
        book1.setTitle("Java编程思想");
        book1.setAuthor("Bruce Eckel");
        book1.setIsbn("9787111213826");
        book1.setPublisher("机械工业出版社");
        book1.setPrice(new BigDecimal("108.00"));
        book1.setCategory(computerCategory);
        book1.setDescription("Java编程经典教程");
        book1.setTotalCopies(10);
        book1.setAvailableCopies(8);
        book1.setBorrowedCopies(2);
        book1.setStatus(1);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Spring实战");
        book2.setAuthor("Craig Walls");
        book2.setIsbn("9787115417305");
        book2.setPublisher("人民邮电出版社");
        book2.setPrice(new BigDecimal("99.00"));
        book2.setCategory(computerCategory);
        book2.setDescription("Spring框架实战指南");
        book2.setTotalCopies(5);
        book2.setAvailableCopies(4);
        book2.setBorrowedCopies(1);
        book2.setStatus(1);
        bookRepository.save(book2);

        Book book3 = new Book();
        book3.setTitle("红楼梦");
        book3.setAuthor("曹雪芹");
        book3.setIsbn("9787020002207");
        book3.setPublisher("人民文学出版社");
        book3.setPrice(new BigDecimal("59.70"));
        book3.setCategory(literatureCategory);
        book3.setDescription("中国古典文学四大名著之一");
        book3.setTotalCopies(8);
        book3.setAvailableCopies(6);
        book3.setBorrowedCopies(2);
        book3.setStatus(1);
        bookRepository.save(book3);

        System.out.println("  ✅ 示例图书创建完成");
    }

    private void initializeUsers() {
        System.out.println("👥 创建示例用户...");

        // 创建示例用户
        User user1 = new User();
        user1.setUsername("student1");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setEmail("student1@campus.edu");
        user1.setRealName("张三");
        user1.setStudentId("2021001");
        user1.setDepartment("计算机学院");
        user1.setMajor("软件工程");
        user1.setRole(User.UserRole.USER);
        user1.setStatus(1);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("student2");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setEmail("student2@campus.edu");
        user2.setRealName("李四");
        user2.setStudentId("2021002");
        user2.setDepartment("文学院");
        user2.setMajor("汉语言文学");
        user2.setRole(User.UserRole.USER);
        user2.setStatus(1);
        userRepository.save(user2);

        System.out.println("  ✅ 示例用户创建完成");
    }
}