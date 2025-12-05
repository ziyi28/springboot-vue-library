package com.library.service;

import com.library.model.User;
import com.library.model.BookCategory;
import com.library.model.Book;
import com.library.repository.UserRepository;
import com.library.repository.BookCategoryRepository;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * 数据库初始化服务
 * 用于创建初始数据
 */
@Service
public class DatabaseInitService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // 注释掉所有初始化代码，让应用先启动成功
        /*
        // 初始化用户数据
        initUsers();

        // 初始化图书分类数据
        initBookCategories();

        // 初始化图书数据
        initBooks();

        System.out.println("✅ 数据库初始化完成");
        */
        System.out.println("✅ 应用启动成功，数据库初始化已暂时跳过");
    }

    private void initUsers() {
        // 检查是否已有用户数据
        if (userRepository.count() > 0) {
            System.out.println("📝 用户数据已存在，跳过初始化");
            return;
        }

        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@library.com");
        admin.setRealName("系统管理员");
        admin.setRole(User.UserRole.ADMIN);
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        userRepository.save(admin);

        // 创建图书管理员用户
        User librarian = new User();
        librarian.setUsername("librarian");
        librarian.setPassword(passwordEncoder.encode("admin123"));
        librarian.setEmail("librarian@library.com");
        librarian.setRealName("图书管理员");
        librarian.setRole(User.UserRole.USER); // 暂时设为普通用户
        librarian.setStatus(1);
        librarian.setCreateTime(LocalDateTime.now());
        librarian.setUpdateTime(LocalDateTime.now());
        userRepository.save(librarian);

        // 创建学生用户
        User student = new User();
        student.setUsername("student1");
        student.setPassword(passwordEncoder.encode("123456"));
        student.setEmail("student1@library.com");
        student.setRealName("张三");
        student.setStudentId("2021001");
        student.setDepartment("计算机学院");
        student.setMajor("软件工程");
        student.setRole(User.UserRole.USER);
        student.setStatus(1);
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        userRepository.save(student);

        // 创建另一个学生用户
        User student2 = new User();
        student2.setUsername("student2");
        student2.setPassword(passwordEncoder.encode("123456"));
        student2.setEmail("student2@library.com");
        student2.setRealName("李四");
        student2.setStudentId("2021002");
        student2.setDepartment("信息学院");
        student2.setMajor("信息管理");
        student2.setRole(User.UserRole.USER);
        student2.setStatus(1);
        student2.setCreateTime(LocalDateTime.now());
        student2.setUpdateTime(LocalDateTime.now());
        userRepository.save(student2);

        System.out.println("👥 创建了4个用户: admin, librarian, student1, student2");
    }

    private void initBookCategories() {
        // 检查是否已有分类数据
        if (bookCategoryRepository.count() > 0) {
            System.out.println("📚 图书分类数据已存在，跳过初始化");
            return;
        }

        String[] categories = {"文学", "科技", "历史", "艺术", "哲学", "经济", "教育", "计算机"};

        for (int i = 0; i < categories.length; i++) {
            BookCategory category = new BookCategory();
            category.setCategoryName(categories[i]);
            category.setCode("C" + String.format("%03d", i + 1));
            category.setDescription(categories[i] + "类图书");
            category.setSortOrder(i + 1);
            category.setStatus(true);
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());
            bookCategoryRepository.save(category);
        }

        System.out.println("📂 创建了8个图书分类");
    }

    private void initBooks() {
        // 检查是否已有图书数据
        if (bookRepository.count() > 0) {
            System.out.println("📖 图书数据已存在，跳过初始化");
            return;
        }

        // 获取第一个分类
        BookCategory literatureCategory = bookCategoryRepository.findById(1L).orElse(null);
        BookCategory techCategory = bookCategoryRepository.findById(2L).orElse(null);
        BookCategory historyCategory = bookCategoryRepository.findById(3L).orElse(null);

        if (literatureCategory != null) {
            Book book1 = new Book();
            book1.setTitle("红楼梦");
            book1.setAuthor("曹雪芹");
            book1.setIsbn("978-7-02-002207-2");
            book1.setPublisher("人民文学出版社");
            book1.setPublishDate(LocalDateTime.of(2000, 1, 1, 0, 0));
            book1.setDescription("中国古典文学四大名著之一");
            book1.setCategory(literatureCategory);
            book1.setTotalCopies(5);
            book1.setAvailableCopies(5);
            book1.setStatus(1);
            book1.setCreateTime(LocalDateTime.now());
            book1.setUpdateTime(LocalDateTime.now());
            bookRepository.save(book1);

            Book book2 = new Book();
            book2.setTitle("三国演义");
            book2.setAuthor("罗贯中");
            book2.setIsbn("978-7-02-002208-9");
            book2.setPublisher("人民文学出版社");
            book2.setPublishDate(LocalDateTime.of(1997, 1, 1, 0, 0));
            book2.setDescription("中国古典文学四大名著之一");
            book2.setCategory(literatureCategory);
            book2.setTotalCopies(3);
            book2.setAvailableCopies(3);
            book2.setStatus(1);
            book2.setCreateTime(LocalDateTime.now());
            book2.setUpdateTime(LocalDateTime.now());
            bookRepository.save(book2);
        }

        if (techCategory != null) {
            Book book3 = new Book();
            book3.setTitle("Java编程思想");
            book3.setAuthor("Bruce Eckel");
            book3.setIsbn("978-7-111-21382-6");
            book3.setPublisher("机械工业出版社");
            book3.setPublishDate(LocalDateTime.of(2007, 6, 1, 0, 0));
            book3.setDescription("Java编程经典教材");
            book3.setCategory(techCategory);
            book3.setTotalCopies(10);
            book3.setAvailableCopies(8);
            book3.setStatus(1);
            book3.setCreateTime(LocalDateTime.now());
            book3.setUpdateTime(LocalDateTime.now());
            bookRepository.save(book3);

            Book book4 = new Book();
            book4.setTitle("Spring实战");
            book4.setAuthor("Craig Walls");
            book4.setIsbn("978-7-115-36741-7");
            book4.setPublisher("人民邮电出版社");
            book4.setPublishDate(LocalDateTime.of(2016, 4, 1, 0, 0));
            book4.setDescription("Spring框架实战指南");
            book4.setCategory(techCategory);
            book4.setTotalCopies(7);
            book4.setAvailableCopies(6);
            book4.setStatus(1);
            book4.setCreateTime(LocalDateTime.now());
            book4.setUpdateTime(LocalDateTime.now());
            bookRepository.save(book4);
        }

        if (historyCategory != null) {
            Book book5 = new Book();
            book5.setTitle("史记");
            book5.setAuthor("司马迁");
            book5.setIsbn("978-7-101-00304-8");
            book5.setPublisher("中华书局");
            book5.setPublishDate(LocalDateTime.of(2006, 6, 1, 0, 0));
            book5.setDescription("中国第一部纪传体通史");
            book5.setCategory(historyCategory);
            book5.setTotalCopies(4);
            book5.setAvailableCopies(4);
            book5.setStatus(1);
            book5.setCreateTime(LocalDateTime.now());
            book5.setUpdateTime(LocalDateTime.now());
            bookRepository.save(book5);
        }

        System.out.println("📚 创建了5本图书");
    }
}