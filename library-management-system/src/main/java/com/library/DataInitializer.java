package com.library;

import com.library.model.User;
import com.library.model.Admin;
import com.library.model.BookCategory;
import com.library.model.Book;
import com.library.service.UserService;
import com.library.service.AdminService;
import com.library.service.BookCategoryService;
import com.library.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 在应用启动时初始化一些基础数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BookCategoryService bookCategoryService;

    @Autowired
    private BookService bookService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化系统数据...");

        // 初始化管理员数据
        initAdminData();

        // 初始化用户数据
        initUserData();

        // 初始化图书分类数据
        initBookCategoryData();

        // 初始化图书数据
        initBookData();

        logger.info("系统数据初始化完成！");
    }

    private void initAdminData() {
        try {
            // 检查是否已有管理员
            if (!adminService.existsByUsername("admin")) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setEmail("admin@library.com");
                admin.setRealName("系统管理员");
                admin.setEmployeeId("ADMIN001");
                admin.setDepartment("系统管理部");
                admin.setRole(Admin.AdminRole.ADMIN);
                admin.setStatus(true);

                adminService.createAdmin(admin);
                logger.info("创建默认管理员账号: admin/admin123");
            } else {
                logger.info("管理员账号已存在");
            }
        } catch (Exception e) {
            logger.error("初始化管理员数据失败: " + e.getMessage());
        }
    }

    private void initUserData() {
        try {
            // 检查是否已有测试用户
            if (!userService.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword("123456");
                user.setEmail("user@library.com");
                user.setRealName("测试用户");
                user.setStudentId("STU001");
                user.setDepartment("计算机学院");
                user.setMajor("软件工程");
                user.setRole(User.UserRole.USER);
                user.setStatus(1);

                userService.register(user);
                logger.info("创建默认用户账号: user/123456");
            } else {
                logger.info("测试用户账号已存在");
            }
        } catch (Exception e) {
            logger.error("初始化用户数据失败: " + e.getMessage());
        }
    }

    private void initBookCategoryData() {
        try {
            String[] categories = {
                "文学小说", "历史传记", "科技科普", "经济管理",
                "教育学习", "艺术设计", "生活休闲", "少儿读物"
            };

            for (String categoryName : categories) {
                // 检查分类是否已存在
                if (!bookCategoryService.existsByCategoryName(categoryName)) {
                    BookCategory category = new BookCategory();
                    category.setCategoryName(categoryName);
                    category.setDescription(categoryName + "相关图书");
                    category.setStatus(true);
                    category.setSortOrder(0);
                    category.setCreateTime(LocalDateTime.now());
                    category.setUpdateTime(LocalDateTime.now());

                    bookCategoryService.save(category);
                    logger.info("创建图书分类: " + categoryName);
                }
            }
        } catch (Exception e) {
            logger.error("初始化图书分类数据失败: " + e.getMessage());
        }
    }

    private void initBookData() {
        try {
            // 获取第一个分类
            List<BookCategory> categories = bookCategoryService.findAllActive();
            if (categories.isEmpty()) {
                logger.warn("没有图书分类，跳过图书初始化");
                return;
            }

            BookCategory firstCategory = categories.get(0);

            // 创建一些示例图书
            String[][] books = {
                {"Java编程思想", "Bruce Eckel", "9787111213826", "机械工业出版社", "89.00"},
                {"Spring实战", "Craig Walls", "9787115417305", "人民邮电出版社", "99.00"},
                {"算法导论", "Thomas H. Cormen", "9787111407010", "机械工业出版社", "128.00"},
                {"设计模式", "Erich Gamma", "9787111075752", "机械工业出版社", "35.00"},
                {"深入理解计算机系统", "Randal E. Bryant", "9787111544937", "机械工业出版社", "139.00"}
            };

            for (String[] bookData : books) {
                String title = bookData[0];
                // 这里简化检查，直接创建图书
                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(bookData[1]);
                book.setIsbn(bookData[2]);
                book.setPublisher(bookData[3]);
                book.setPrice(new BigDecimal(bookData[4]));
                book.setCategory(firstCategory);
                book.setDescription(title + "的详细描述");
                book.setTotalCopies(5);
                book.setAvailableCopies(5);
                book.setBorrowedCopies(0);
                book.setStatus(1);
                book.setCreateTime(LocalDateTime.now());
                book.setUpdateTime(LocalDateTime.now());

                bookService.addBook(book);
                logger.info("创建图书: " + title);
            }
        } catch (Exception e) {
            logger.error("初始化图书数据失败: " + e.getMessage());
        }
    }
}