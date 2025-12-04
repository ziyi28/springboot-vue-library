package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.*;
import com.library.mapper.*;
import com.library.service.impl.BorrowRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 借阅记录服务测试类
 * 测试范围：借书、还书、续借、逾期管理
 */
@ExtendWith(MockitoExtension.class)
class BorrowRecordServiceTest {

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @Mock
    private BookService bookService;

    @Mock
    private BookRecordMapper bookRecordMapper;

    @Mock
    private UserService userService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BorrowRecordServiceImpl borrowRecordService;

    private User testUser;
    private Book testBook;
    private BookRecord testBookRecord;
    private BorrowRecord testBorrowRecord;

    @BeforeEach
    void setUp() {
        // 测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setStatus(1);

        // 测试图书
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Java核心技术");
        testBook.setAuthor("Cay S. Horstmann");
        testBook.setTotalCopies(10);
        testBook.setAvailableCopies(8);
        testBook.setStatus(1);

        // 测试图书副本
        testBookRecord = new BookRecord();
        testBookRecord.setId(1L);
        testBookRecord.setBookId(1L);
        testBookRecord.setBarcode("ISBN001");
        testBookRecord.setStatus(1); // 可借

        // 测试借阅记录
        testBorrowRecord = new BorrowRecord();
        testBorrowRecord.setId(1L);
        testBorrowRecord.setUserId(1L);
        testBorrowRecord.setBookId(1L);
        testBorrowRecord.setBookRecordId(1L);
        testBorrowRecord.setBorrowDate(LocalDateTime.now());
        testBorrowRecord.setDueDate(LocalDateTime.now().plusDays(30));
        testBorrowRecord.setRenewCount(0);
        testBorrowRecord.setStatus(1); // 借阅中
        testBorrowRecord.setFineAmount(BigDecimal.ZERO);
    }

    @Test
    void testGetBorrowRecordsWithDetails_成功查询() {
        // Given
        Page<BorrowRecord> page = new Page<>(1, 10);
        List<BorrowRecord> records = Arrays.asList(testBorrowRecord);
        page.setRecords(records);

        when(borrowRecordMapper.selectBorrowRecordsWithDetails(any(), eq(1L), eq(1))).thenReturn(page);

        // When
        Page<BorrowRecord> result = borrowRecordService.getBorrowRecordsWithDetails(new Page<>(1, 10), 1L, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(borrowRecordMapper).selectBorrowRecordsWithDetails(any(), eq(1L), eq(1));
    }

    @Test
    void testBorrowBook_成功借阅() {
        // Given
        when(userService.getById(1L)).thenReturn(testUser);
        when(bookService.getById(1L)).thenReturn(testBook);
        when(borrowRecordMapper.selectOne(any())).thenReturn(null); // 没有未归还的记录
        when(bookRecordMapper.selectOne(any())).thenReturn(testBookRecord);
        when(bookRecordMapper.updateById(any())).thenReturn(1);
        when(bookService.updateById(any())).thenReturn(1);
        when(borrowRecordMapper.insert(any())).thenReturn(1);

        // When
        boolean result = borrowRecordService.borrowBook(1L, 1L);

        // Then
        assertTrue(result);
        verify(userService).getById(1L);
        verify(bookService).getById(1L);
        verify(bookRecordMapper).selectOne(any());
        verify(bookRecordMapper).updateById(any());
        verify(bookService).updateById(any());
        verify(borrowRecordMapper).insert(any());
    }

    @Test
    void testBorrowBook_用户不存在() {
        // Given
        when(userService.getById(1L)).thenReturn(null);

        // When
        boolean result = borrowRecordService.borrowBook(1L, 1L);

        // Then
        assertFalse(result);
        verify(userService).getById(1L);
        verify(bookService, never()).getById(any());
    }

    @Test
    void testBorrowBook_用户被禁用() {
        // Given
        testUser.setStatus(0); // 禁用状态
        when(userService.getById(1L)).thenReturn(testUser);

        // When
        boolean result = borrowRecordService.borrowBook(1L, 1L);

        // Then
        assertFalse(result);
        verify(userService).getById(1L);
        verify(bookService, never()).getById(any());
    }

    @Test
    void testBorrowBook_图书不存在() {
        // Given
        when(userService.getById(1L)).thenReturn(testUser);
        when(bookService.getById(1L)).thenReturn(null);

        // When
        boolean result = borrowRecordService.borrowBook(1L, 1L);

        // Then
        assertFalse(result);
        verify(userService).getById(1L);
        verify(bookService).getById(1L);
    }

    @Test
    void testBorrowBook_图书无可借副本() {
        // Given
        testBook.setAvailableCopies(0); // 没有可借副本
        when(userService.getById(1L)).thenReturn(testUser);
        when(bookService.getById(1L)).thenReturn(testBook);

        // When
        boolean result = borrowRecordService.borrowBook(1L, 1L);

        // Then
        assertFalse(result);
        verify(userService).getById(1L);
        verify(bookService).getById(1L);
    }

    @Test
    void testBorrowBook_已借阅未归还() {
        // Given
        when(userService.getById(1L)).thenReturn(testUser);
        when(bookService.getById(1L)).thenReturn(testBook);
        when(borrowRecordMapper.selectOne(any())).thenReturn(testBorrowRecord); // 有未归还记录

        // When
        boolean result = borrowRecordService.borrowBook(1L, 1L);

        // Then
        assertFalse(result);
        verify(userService).getById(1L);
        verify(bookService).getById(1L);
        verify(borrowRecordMapper).selectOne(any());
        verify(bookRecordMapper, never()).selectOne(any());
    }

    @Test
    void testReturnBook_成功归还() {
        // Given
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);
        when(borrowRecordMapper.updateById(any())).thenReturn(1);
        when(bookRecordMapper.selectById(1L)).thenReturn(testBookRecord);
        when(bookRecordMapper.updateById(any())).thenReturn(1);
        when(bookService.getById(1L)).thenReturn(testBook);
        when(bookService.updateById(any())).thenReturn(1);

        // When
        boolean result = borrowRecordService.returnBook(1L, 1L);

        // Then
        assertTrue(result);
        assertEquals(Integer.valueOf(2), testBorrowRecord.getStatus()); // 已归还
        assertNotNull(testBorrowRecord.getReturnDate()); // 归还时间不为空
        verify(borrowRecordMapper).selectById(1L);
        verify(borrowRecordMapper).updateById(any());
        verify(bookRecordMapper).selectById(1L);
        verify(bookRecordMapper).updateById(any());
        verify(bookService).getById(1L);
        verify(bookService).updateById(any());
    }

    @Test
    void testReturnBook_记录不存在() {
        // Given
        when(borrowRecordMapper.selectById(1L)).thenReturn(null);

        // When
        boolean result = borrowRecordService.returnBook(1L, 1L);

        // Then
        assertFalse(result);
        verify(borrowRecordMapper).selectById(1L);
        verify(borrowRecordMapper, never()).updateById(any());
    }

    @Test
    void testReturnBook_用户不匹配() {
        // Given
        testBorrowRecord.setUserId(2L); // 不同用户
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        // When
        boolean result = borrowRecordService.returnBook(1L, 1L);

        // Then
        assertFalse(result);
        verify(borrowRecordMapper).selectById(1L);
        verify(borrowRecordMapper, never()).updateById(any());
    }

    @Test
    void testRenewBook_成功续借() {
        // Given
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);
        when(borrowRecordMapper.updateById(any())).thenReturn(1);

        // When
        boolean result = borrowRecordService.renewBook(1L, 1L);

        // Then
        assertTrue(result);
        assertEquals(Integer.valueOf(1), testBorrowRecord.getRenewCount()); // 续借次数+1
        verify(borrowRecordMapper).selectById(1L);
        verify(borrowRecordMapper).updateById(any());
    }

    @Test
    void testRenewBook_超过续借次数限制() {
        // Given
        testBorrowRecord.setRenewCount(2); // 已续借2次，达到限制
        when(borrowRecordMapper.selectById(1L)).thenReturn(testBorrowRecord);

        // When
        boolean result = borrowRecordService.renewBook(1L, 1L);

        // Then
        assertFalse(result);
        assertEquals(Integer.valueOf(2), testBorrowRecord.getRenewCount()); // 次数不变
        verify(borrowRecordMapper).selectById(1L);
        verify(borrowRecordMapper, never()).updateById(any());
    }

    @Test
    void testGetOverdueRecords_获取逾期记录() {
        // Given
        testBorrowRecord.setDueDate(LocalDateTime.now().minusDays(1)); // 逾期
        List<BorrowRecord> overdueRecords = Arrays.asList(testBorrowRecord);
        when(borrowRecordMapper.selectList(any())).thenReturn(overdueRecords);

        // When
        List<BorrowRecord> result = borrowRecordService.getOverdueRecords();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(borrowRecordMapper).selectList(any());
    }

    @Test
    void testUpdateOverdueRecords_成功更新逾期状态() {
        // Given
        testBorrowRecord.setDueDate(LocalDateTime.now().minusDays(2)); // 逾期2天
        List<BorrowRecord> overdueRecords = Arrays.asList(testBorrowRecord);
        when(borrowRecordMapper.selectList(any())).thenReturn(overdueRecords);
        when(borrowRecordMapper.updateBatchById(any())).thenReturn(true);

        // When
        boolean result = borrowRecordService.updateOverdueRecords();

        // Then
        assertTrue(result);
        assertEquals(Integer.valueOf(3), testBorrowRecord.getStatus()); // 逾期状态
        assertEquals(new BigDecimal("1.00"), testBorrowRecord.getFineAmount()); // 罚金1.00元
        verify(borrowRecordMapper).selectList(any());
        verify(borrowRecordMapper).updateBatchById(any());
    }

    @Test
    void testUpdateOverdueRecords_无逾期记录() {
        // Given
        when(borrowRecordMapper.selectList(any())).thenReturn(Arrays.asList());

        // When
        boolean result = borrowRecordService.updateOverdueRecords();

        // Then
        assertTrue(result);
        verify(borrowRecordMapper).selectList(any());
        verify(borrowRecordMapper, never()).updateBatchById(any());
    }
}