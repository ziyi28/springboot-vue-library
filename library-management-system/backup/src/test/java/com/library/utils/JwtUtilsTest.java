package com.library.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 * 测试范围：Token生成、解析、验证
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String testSecret = "test-secret-key-for-jwt-testing";
    private final long testExpiration = 86400000L; // 24小时

    @BeforeEach
    void setUp() {
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(jwtUtils, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "expiration", testExpiration);
    }

    @Test
    void testGenerateToken_成功生成() {
        // Given
        String username = "testuser";
        String role = "USER";

        // When
        String token = jwtUtils.generateToken(username, role);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT应该有三部分
    }

    @Test
    void testGetClaimsFromToken_有效Token() {
        // Given
        String username = "testuser";
        String role = "ADMIN";
        String token = jwtUtils.generateToken(username, role);

        // When
        var claims = jwtUtils.getClaimsFromToken(token);

        // Then
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(role, claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void testGetClaimsFromToken_无效Token() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        var claims = jwtUtils.getClaimsFromToken(invalidToken);

        // Then
        assertNull(claims);
    }

    @Test
    void testGetUsernameFromToken_成功获取() {
        // Given
        String username = "testuser";
        String token = jwtUtils.generateToken(username, "USER");

        // When
        String result = jwtUtils.getUsernameFromToken(token);

        // Then
        assertEquals(username, result);
    }

    @Test
    void testGetUsernameFromToken_无效Token() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        String result = jwtUtils.getUsernameFromToken(invalidToken);

        // Then
        assertNull(result);
    }

    @Test
    void testGetRoleFromToken_成功获取() {
        // Given
        String role = "ADMIN";
        String token = jwtUtils.generateToken("testuser", role);

        // When
        String result = jwtUtils.getRoleFromToken(token);

        // Then
        assertEquals(role, result);
    }

    @Test
    void testGetRoleFromToken_无效Token() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        String result = jwtUtils.getRoleFromToken(invalidToken);

        // Then
        assertNull(result);
    }

    @Test
    void testIsTokenExpired_未过期() {
        // Given
        String token = jwtUtils.generateToken("testuser", "USER");

        // When
        boolean result = jwtUtils.isTokenExpired(token);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsTokenExpired_已过期() throws InterruptedException {
        // Given - 创建一个短期的token
        ReflectionTestUtils.setField(jwtUtils, "expiration", 1L); // 1毫秒
        String token = jwtUtils.generateToken("testuser", "USER");

        // 等待token过期
        Thread.sleep(10);

        // When
        boolean result = jwtUtils.isTokenExpired(token);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsTokenExpired_无效Token() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean result = jwtUtils.isTokenExpired(invalidToken);

        // Then
        assertTrue(result); // 无效token被认为已过期
    }

    @Test
    void testValidateToken_有效Token() {
        // Given
        String token = jwtUtils.generateToken("testuser", "USER");

        // When
        boolean result = jwtUtils.validateToken(token);

        // Then
        assertTrue(result);
    }

    @Test
    void testValidateToken_无效Token() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean result = jwtUtils.validateToken(invalidToken);

        // Then
        assertFalse(result);
    }

    @Test
    void testValidateToken_空Token() {
        // Given
        String emptyToken = "";

        // When
        boolean result = jwtUtils.validateToken(emptyToken);

        // Then
        assertFalse(result);
    }

    @Test
    void testValidateToken_nullToken() {
        // Given
        String nullToken = null;

        // When
        boolean result = jwtUtils.validateToken(nullToken);

        // Then
        assertFalse(result);
    }

    @Test
    void testToken一致性_同一用户生成的Token不同() {
        // Given
        String username = "testuser";
        String role = "USER";

        // When
        String token1 = jwtUtils.generateToken(username, role);
        String token2 = jwtUtils.generateToken(username, role);

        // Then
        assertNotEquals(token1, token2); // 由于包含时间戳，每次生成的token应该不同
        assertEquals(username, jwtUtils.getUsernameFromToken(token1));
        assertEquals(username, jwtUtils.getUsernameFromToken(token2));
        assertEquals(role, jwtUtils.getRoleFromToken(token1));
        assertEquals(role, jwtUtils.getRoleFromToken(token2));
    }

    @Test
    void testToken解析_不同角色() {
        // Given
        String adminToken = jwtUtils.generateToken("admin", "ADMIN");
        String userToken = jwtUtils.generateToken("user", "USER");

        // When
        String adminRole = jwtUtils.getRoleFromToken(adminToken);
        String userRole = jwtUtils.getRoleFromToken(userToken);

        // Then
        assertEquals("ADMIN", adminRole);
        assertEquals("USER", userRole);
    }

    @Test
    void testToken解析_特殊字符用户名() {
        // Given
        String specialUsername = "test@user.com";
        String role = "USER";
        String token = jwtUtils.generateToken(specialUsername, role);

        // When
        String result = jwtUtils.getUsernameFromToken(token);

        // Then
        assertEquals(specialUsername, result);
    }
}