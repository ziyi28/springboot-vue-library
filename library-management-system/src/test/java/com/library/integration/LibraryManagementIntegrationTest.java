package com.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.entity.Book;
import com.library.entity.BookCategory;
import com.library.entity.User;
import com.library.service.BookCategoryService;
import com.library.service.BookService;
import com.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 图书管理系统集成测试
 * 测试完整的业务流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@Transactional
class LibraryManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCategoryService bookCategoryService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;
    private User testUser;
    private BookCategory testCategory;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("integrationtest");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setEmail("integration@test.com");
        testUser.setRealName("集成测试用户");
        testUser.setStudentId("INTEG001");
        userService.register(testUser);

        // 获取用户token
        try {
            String loginResponse = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"integrationtest\",\"password\":\"password123\",\"type\":\"user\"}"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            userToken = extractTokenFromResponse(loginResponse);
        } catch (Exception e) {
            // 如果获取token失败，设置为空
            userToken = "";
        }

        // 获取管理员token
        try {
            String adminLoginResponse = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"admin\",\"password\":\"admin123\",\"type\":\"admin\"}"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            adminToken = extractTokenFromResponse(adminLoginResponse);
        } catch (Exception e) {
            adminToken = "";
        }

        // 创建测试分类
        testCategory = new BookCategory();
        testCategory.setCategoryName("测试分类");
        testCategory.setCategoryCode("TEST");
        testCategory.setDescription("集成测试用分类");
        bookCategoryService.save(testCategory);

        // 创建测试图书
        testBook = new Book();
        testBook.setTitle("集成测试图书");
        testBook.setAuthor("测试作者");
        testBook.setPublisher("测试出版社");
        testBook.setCategoryId(testCategory.getId());
        testBook.setTotalCopies(5);
        testBook.setAvailableCopies(5);
        bookService.save(testBook);
    }

    private String extractTokenFromResponse(String response) {
        try {
            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(response);
            return node.path("data").path("token").asText();
        } catch (Exception e) {
            return "";
        }
    }

    @Test
    void testCompleteBorrowFlow() throws Exception {
        // 1. 用户登录获取token
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"integrationtest\",\"password\":\"password123\",\"type\":\"user\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.username").value("integrationtest"))
                .andReturn().getResponse().getContentAsString();

        String token = extractTokenFromResponse(loginResponse);

        // 2. 查看可借图书列表
        mockMvc.perform(get("/api/books/list")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records").isArray());

        // 3. 借阅图书
        mockMvc.perform(post("/api/borrow-records/borrow/" + testBook.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("借阅成功"));

        // 4. 查看借阅记录
        mockMvc.perform(get("/api/borrow-records/my-records")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records").isArray());

        // 5. 归还图书
        mockMvc.perform(post("/api/borrow-records/return/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("归还成功"));
    }

    @Test
    void testAdminBookManagementFlow() throws Exception {
        // 1. 管理员添加图书
        Book newBook = new Book();
        newBook.setTitle("新书测试");
        newBook.setAuthor("新作者");
        newBook.setPublisher("新出版社");
        newBook.setCategoryId(testCategory.getId());
        newBook.setTotalCopies(3);
        newBook.setAvailableCopies(3);

        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("添加成功"));

        // 2. 查看图书列表验证添加成功
        mockMvc.perform(get("/api/books/list")
                .param("title", "新书测试")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[?(@.title == '新书测试')]").exists());

        // 3. 更新图书信息
        newBook.setTitle("更新后的书名");
        mockMvc.perform(put("/api/books/" + testBook.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("更新成功"));

        // 4. 删除图书
        mockMvc.perform(delete("/api/books/" + testBook.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testUserRegistrationAndProfileManagement() throws Exception {
        // 1. 新用户注册
        User newUser = new User();
        newUser.setUsername("newintegrationuser");
        newUser.setPassword("newpass123");
        newUser.setEmail("new@integration.com");
        newUser.setRealName("新集成用户");
        newUser.setStudentId("NEW001");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("注册成功"));

        // 2. 用户登录
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newintegrationuser\",\"password\":\"newpass123\",\"type\":\"user\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = extractTokenFromResponse(loginResponse);

        // 3. 获取用户详情
        mockMvc.perform(get("/api/users/2")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("newintegrationuser"));

        // 4. 修改用户信息
        User updatedUser = new User();
        updatedUser.setRealName("更新后的姓名");
        updatedUser.setEmail("updated@integration.com");

        mockMvc.perform(put("/api/users/2")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testBookCategoryManagement() throws Exception {
        // 1. 获取分类列表
        mockMvc.perform(get("/api/categories/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        // 2. 管理员添加分类
        BookCategory newCategory = new BookCategory();
        newCategory.setCategoryName("新分类");
        newCategory.setCategoryCode("NEWCAT");
        newCategory.setDescription("新增的分类");

        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("添加成功"));

        // 3. 获取所有分类（管理员）
        mockMvc.perform(get("/api/categories/all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.categoryCode == 'NEWCAT')]").exists());
    }

    @Test
    void testBookSearchFunctionality() throws Exception {
        // 1. 按标题搜索
        mockMvc.perform(get("/api/books/search")
                .param("keyword", "集成"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        // 2. 获取热门图书
        mockMvc.perform(get("/api/books/popular")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        // 3. 分页查询图书列表
        mockMvc.perform(get("/api/books/list")
                .param("current", "1")
                .param("size", "5")
                .param("title", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.current").value(1))
                .andExpect(jsonPath("$.data.size").value(5));
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // 1. 未授权访问受保护的接口
        mockMvc.perform(get("/api/users/list"))
                .andExpect(status().isUnauthorized());

        // 2. 使用无效token访问
        mockMvc.perform(get("/api/users/list")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        // 3. 普通用户尝试访问管理员接口
        mockMvc.perform(delete("/api/books/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRenewBookFunctionality() throws Exception {
        // 1. 先借阅图书
        mockMvc.perform(post("/api/borrow-records/borrow/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // 2. 续借图书
        mockMvc.perform(post("/api/borrow-records/renew/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("续借成功"));

        // 3. 尝试第二次续借
        mockMvc.perform(post("/api/borrow-records/renew/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // 4. 尝试第三次续借（应该失败，超过限制）
        mockMvc.perform(post("/api/borrow-records/renew/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("续借失败，请检查是否超过续借次数限制"));
    }

    @Test
    void testBorrowSameBookTwice() throws Exception {
        // 1. 第一次借阅图书
        mockMvc.perform(post("/api/borrow-records/borrow/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("借阅成功"));

        // 2. 第二次借阅同一本书（应该失败）
        mockMvc.perform(post("/api/borrow-records/borrow/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("借阅失败，请检查图书是否可借或您是否已借阅该书"));
    }
}