package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.*;
import com.library.mapper.*;
import com.library.service.BookRecordMapper;
import com.library.service.BookService;
import com.library.service.BorrowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    @Resource
    private BorrowRecordMapper borrowRecordMapper;

    @Resource
    private BookService bookService;

    @Resource
    private BookRecordMapper bookRecordMapper;

    @Resource
    private UserService userService;

    @Resource
    private BookMapper bookMapper;

    @Override
    public Page<BorrowRecord> getBorrowRecordsWithDetails(Page<BorrowRecord> page, Long userId, Integer status) {
        return borrowRecordMapper.selectBorrowRecordsWithDetails(page, userId, status);
    }

    @Override
    @Transactional
    public boolean borrowBook(Long userId, Long bookId) {
        try {
            // 检查用户是否存在且状态正常
            User user = userService.getById(userId);
            if (user == null || user.getStatus() != 1) {
                return false;
            }

            // 检查图书是否存在且有可借副本
            Book book = bookService.getById(bookId);
            if (book == null || book.getStatus() != 1 || book.getAvailableCopies() <= 0) {
                return false;
            }

            // 检查用户是否已经借过这本书且未归还
            QueryWrapper<BorrowRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId)
                   .eq("book_id", bookId)
                   .eq("status", 1);
            if (this.count(wrapper) > 0) {
                return false; // 已经借过且未归还
            }

            // 获取一个可借的图书副本
            QueryWrapper<BookRecord> recordWrapper = new QueryWrapper<>();
            recordWrapper.eq("book_id", bookId)
                         .eq("status", 1)
                         .last("LIMIT 1");
            BookRecord bookRecord = bookRecordMapper.selectOne(recordWrapper);

            if (bookRecord == null) {
                return false;
            }

            // 更新副本状态为借出
            bookRecord.setStatus(0);
            bookRecordMapper.updateById(bookRecord);

            // 更新图书可借数量
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookService.updateById(book);

            // 创建借阅记录
            BorrowRecord borrowRecord = new BorrowRecord();
            borrowRecord.setUserId(userId);
            borrowRecord.setBookId(bookId);
            borrowRecord.setBookRecordId(bookRecord.getId());
            borrowRecord.setBorrowDate(LocalDateTime.now());
            borrowRecord.setDueDate(LocalDateTime.now().plusDays(30)); // 默认借阅30天
            borrowRecord.setRenewCount(0);
            borrowRecord.setStatus(1); // 借阅中
            borrowRecord.setFineAmount(BigDecimal.ZERO);

            return this.save(borrowRecord);

        } catch (Exception e) {
            log.error("借阅图书失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean returnBook(Long userId, Long recordId) {
        try {
            BorrowRecord borrowRecord = this.getById(recordId);
            if (borrowRecord == null || !borrowRecord.getUserId().equals(userId) || borrowRecord.getStatus() != 1) {
                return false;
            }

            // 更新借阅记录状态
            borrowRecord.setStatus(2); // 已归还
            borrowRecord.setReturnDate(LocalDateTime.now());
            this.updateById(borrowRecord);

            // 更新副本状态为可借
            BookRecord bookRecord = bookRecordMapper.selectById(borrowRecord.getBookRecordId());
            if (bookRecord != null) {
                bookRecord.setStatus(1);
                bookRecordMapper.updateById(bookRecord);
            }

            // 更新图书可借数量
            Book book = bookService.getById(borrowRecord.getBookId());
            if (book != null) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookService.updateById(book);
            }

            return true;

        } catch (Exception e) {
            log.error("归还图书失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean renewBook(Long userId, Long recordId) {
        try {
            BorrowRecord borrowRecord = this.getById(recordId);
            if (borrowRecord == null || !borrowRecord.getUserId().equals(userId) || borrowRecord.getStatus() != 1) {
                return false;
            }

            // 检查续借次数限制（最多续借2次）
            if (borrowRecord.getRenewCount() >= 2) {
                return false;
            }

            // 延长归还时间15天
            borrowRecord.setDueDate(borrowRecord.getDueDate().plusDays(15));
            borrowRecord.setRenewCount(borrowRecord.getRenewCount() + 1);

            return this.updateById(borrowRecord);

        } catch (Exception e) {
            log.error("续借图书失败", e);
            return false;
        }
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        QueryWrapper<BorrowRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
               .lt("due_date", LocalDateTime.now());
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public boolean updateOverdueRecords() {
        try {
            QueryWrapper<BorrowRecord> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1)
                   .lt("due_date", LocalDateTime.now());

            List<BorrowRecord> overdueRecords = this.list(wrapper);
            for (BorrowRecord record : overdueRecords) {
                record.setStatus(3); // 逾期
                // 计算罚金（每天0.5元）
                long overdueDays = java.time.Duration.between(record.getDueDate(), LocalDateTime.now()).toDays();
                BigDecimal fine = BigDecimal.valueOf(overdueDays * 0.5);
                record.setFineAmount(fine);
            }

            return this.updateBatchById(overdueRecords);

        } catch (Exception e) {
            log.error("更新逾期记录失败", e);
            return false;
        }
    }
}