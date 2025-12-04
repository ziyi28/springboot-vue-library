package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 * 测试范围：用户注册、登录、查询、修改密码、状态管理
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC");
        testUser.setEmail("test@example.com");
        testUser.setRealName("测试用户");
        testUser.setStudentId("20230001");
        testUser.setStatus(1);
    }

    @Test
    void testFindByUsername_存在用户() {
        // Given
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper).selectOne(any());
    }

    @Test
    void testFindByUsername_不存在用户() {
        // Given
        when(userMapper.selectOne(any())).thenReturn(null);

        // When
        User result = userService.findByUsername("nonexistent");

        // Then
        assertNull(result);
        verify(userMapper).selectOne(any());
    }

    @Test
    void testGetUserList_有关键词() {
        // Given
        Page<User> page = new Page<>(1, 10);
        List<User> userList = Arrays.asList(testUser);
        page.setRecords(userList);

        when(userMapper.selectPage(any(), any())).thenReturn(page);

        // When
        Page<User> result = userService.getUserList(new Page<>(1, 10), "test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(userMapper).selectPage(any(), any());
    }

    @Test
    void testRegister_成功注册() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEmail("new@example.com");

        when(userMapper.selectOne(any())).thenReturn(null);
        when(userMapper.insert(any())).thenReturn(1);

        // When
        boolean result = userService.register(newUser);

        // Then
        assertTrue(result);
        verify(userMapper).selectOne(any());
        verify(userMapper).insert(any());
    }

    @Test
    void testRegister_用户名已存在() {
        // Given
        User newUser = new User();
        newUser.setUsername("testuser"); // 已存在的用户名
        newUser.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser);

        // When
        boolean result = userService.register(newUser);

        // Then
        assertFalse(result);
        verify(userMapper).selectOne(any());
        verify(userMapper, never()).insert(any());
    }

    @Test
    void testUpdatePassword_成功修改() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any())).thenReturn(1);

        // When
        boolean result = userService.updatePassword(1L, "admin123", "newpassword123");

        // Then
        assertTrue(result);
        verify(userMapper).selectById(1L);
        verify(userMapper).updateById(any());
    }

    @Test
    void testUpdatePassword_原密码错误() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        boolean result = userService.updatePassword(1L, "wrongpassword", "newpassword123");

        // Then
        assertFalse(result);
        verify(userMapper).selectById(1L);
        verify(userMapper, never()).updateById(any());
    }

    @Test
    void testUpdatePassword_用户不存在() {
        // Given
        when(userMapper.selectById(999L)).thenReturn(null);

        // When
        boolean result = userService.updatePassword(999L, "admin123", "newpassword123");

        // Then
        assertFalse(result);
        verify(userMapper).selectById(999L);
        verify(userMapper, never()).updateById(any());
    }

    @Test
    void testUpdateUserStatus_成功更新() {
        // Given
        when(userMapper.updateById(any())).thenReturn(1);

        // When
        boolean result = userService.updateUserStatus(1L, 0);

        // Then
        assertTrue(result);
        verify(userMapper).updateById(any());
    }

    @Test
    void testUpdateUserStatus_更新失败() {
        // Given
        when(userMapper.updateById(any())).thenReturn(0);

        // When
        boolean result = userService.updateUserStatus(1L, 0);

        // Then
        assertFalse(result);
        verify(userMapper).updateById(any());
    }
}