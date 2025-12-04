package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 图书服务测试类
 * 测试范围：图书增删改查、搜索、库存管理
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setIsbn("9787111213826");
        testBook.setTitle("Java核心技术");
        testBook.setAuthor("Cay S. Horstmann");
        testBook.setPublisher("机械工业出版社");
        testBook.setPublishDate(LocalDate.of(2018, 9, 1));
        testBook.setCategoryId(2L);
        testBook.setDescription("Java技术经典参考书");
        testBook.setPageCount(800);
        testBook.setLanguage("中文");
        testBook.setPrice(new BigDecimal("119.00"));
        testBook.setTotalCopies(10);
        testBook.setAvailableCopies(8);
        testBook.setStatus(1);
    }

    @Test
    void testGetBookPage_成功分页查询() {
        // Given
        Page<Book> page = new Page<>(1, 10);
        List<Book> bookList = Arrays.asList(testBook);
        page.setRecords(bookList);

        when(bookMapper.selectBooksWithCategory(any(), eq("Java"), eq("Cay"), eq(2L))).thenReturn(page);

        // When
        Page<Book> result = bookService.getBookPage(new Page<>(1, 10), "Java", "Cay", 2L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("Java核心技术", result.getRecords().get(0).getTitle());
        verify(bookMapper).selectBooksWithCategory(any(), eq("Java"), eq("Cay"), eq(2L));
    }

    @Test
    void testGetBookPage_无参数查询() {
        // Given
        Page<Book> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testBook));

        when(bookMapper.selectBooksWithCategory(any(), eq(null), eq(null), eq(null))).thenReturn(page);

        // When
        Page<Book> result = bookService.getBookPage(new Page<>(1, 10), null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(bookMapper).selectBooksWithCategory(any(), eq(null), eq(null), eq(null));
    }

    @Test
    void testAddBook_成功添加() {
        // Given
        Book newBook = new Book();
        newBook.setTitle("新书");
        newBook.setAuthor("新作者");
        newBook.setTotalCopies(5);
        newBook.setCategoryId(1L);

        when(bookMapper.insert(any())).thenReturn(1);

        // When
        boolean result = bookService.addBook(newBook);

        // Then
        assertTrue(result);
        assertEquals(5, newBook.getAvailableCopies()); // 可借数量应该等于总数量
        assertEquals(Integer.valueOf(1), newBook.getStatus()); // 状态应该是上架
        verify(bookMapper).insert(any());
    }

    @Test
    void testUpdateAvailableCopies_成功增加() {
        // Given
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(bookMapper.updateById(any())).thenReturn(1);

        // When
        boolean result = bookService.updateAvailableCopies(1L, 2);

        // Then
        assertTrue(result);
        assertEquals(10, testBook.getAvailableCopies()); // 8 + 2 = 10
        verify(bookMapper).selectById(1L);
        verify(bookMapper).updateById(any());
    }

    @Test
    void testUpdateAvailableCopies_成功减少() {
        // Given
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(bookMapper.updateById(any())).thenReturn(1);

        // When
        boolean result = bookService.updateAvailableCopies(1L, -3);

        // Then
        assertTrue(result);
        assertEquals(5, testBook.getAvailableCopies()); // 8 - 3 = 5
        verify(bookMapper).selectById(1L);
        verify(bookMapper).updateById(any());
    }

    @Test
    void testUpdateAvailableCopies_超出范围() {
        // Given
        when(bookMapper.selectById(1L)).thenReturn(testBook);

        // When
        boolean result = bookService.updateAvailableCopies(1L, 5); // 8 + 5 = 13 > 10

        // Then
        assertFalse(result);
        assertEquals(8, testBook.getAvailableCopies()); // 数量不变
        verify(bookMapper).selectById(1L);
        verify(bookMapper, never()).updateById(any());
    }

    @Test
    void testUpdateAvailableCopies_负数() {
        // Given
        when(bookMapper.selectById(1L)).thenReturn(testBook);

        // When
        boolean result = bookService.updateAvailableCopies(1L, -10); // 8 - 10 = -2 < 0

        // Then
        assertFalse(result);
        assertEquals(8, testBook.getAvailableCopies()); // 数量不变
        verify(bookMapper).selectById(1L);
        verify(bookMapper, never()).updateById(any());
    }

    @Test
    void testUpdateAvailableCopies_图书不存在() {
        // Given
        when(bookMapper.selectById(999L)).thenReturn(null);

        // When
        boolean result = bookService.updateAvailableCopies(999L, 1);

        // Then
        assertFalse(result);
        verify(bookMapper).selectById(999L);
        verify(bookMapper, never()).updateById(any());
    }

    @Test
    void testGetPopularBooks_成功获取() {
        // Given
        List<Book> popularBooks = Arrays.asList(testBook);
        when(bookMapper.selectList(any())).thenReturn(popularBooks);

        // When
        List<Book> result = bookService.getPopularBooks(5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookMapper).selectList(any());
    }

    @Test
    void testSearchBooks_有关键词() {
        // Given
        List<Book> searchResults = Arrays.asList(testBook);
        when(bookMapper.selectList(any())).thenReturn(searchResults);

        // When
        List<Book> result = bookService.searchBooks("Java");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookMapper).selectList(any());
    }

    @Test
    void testSearchBooks_空关键词() {
        // Given
        List<Book> allBooks = Arrays.asList(testBook);
        when(bookMapper.selectList(any())).thenReturn(allBooks);

        // When
        List<Book> result = bookService.searchBooks("");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookMapper).selectList(any());
    }

    @Test
    void testGetBookWithCategory_成功获取() {
        // Given
        Page<Book> page = new Page<>(1, 1);
        page.setRecords(Arrays.asList(testBook));

        when(bookMapper.selectBooksWithCategory(any(), eq(null), eq(null), eq(null))).thenReturn(page);

        // When
        Book result = bookService.getBookWithCategory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookMapper).selectBooksWithCategory(any(), eq(null), eq(null), eq(null));
    }

    @Test
    void testGetBookWithCategory_图书不存在() {
        // Given
        Page<Book> page = new Page<>(1, 1);
        page.setRecords(Arrays.asList()); // 空列表

        when(bookMapper.selectBooksWithCategory(any(), eq(null), eq(null), eq(null))).thenReturn(page);

        // When
        Book result = bookService.getBookWithCategory(999L);

        // Then
        assertNull(result);
        verify(bookMapper).selectBooksWithCategory(any(), eq(null), eq(null), eq(null));
    }
}