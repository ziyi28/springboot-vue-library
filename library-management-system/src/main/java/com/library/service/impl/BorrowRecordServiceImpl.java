package com.library.service.impl;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import com.library.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowRecordServiceImpl implements BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // 每个用户最多同时借阅5本书
    private static final int MAX_BORROW_LIMIT = 5;

    @Override
    @Transactional
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        // 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查图书是否存在
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 检查用户是否可以借阅
        if (!canUserBorrow(userId)) {
            throw new RuntimeException("用户已达到借阅上限或借阅权限受限");
        }

        // 检查图书是否可借
        if (!isBookAvailable(bookId)) {
            throw new RuntimeException("图书无可借副本或已下架");
        }

        // 检查用户是否已经借阅了这本图书
        BorrowRecord existingRecord = borrowRecordRepository
                .findCurrentBorrowByUserAndBook(userId, bookId);
        if (existingRecord != null) {
            throw new RuntimeException("用户已借阅此图书，请勿重复借阅");
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord(user, book);
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        // 更新图书的借阅数量
        book.setBorrowedCopies(book.getBorrowedCopies() + 1);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setUpdateTime(LocalDateTime.now());
        bookRepository.save(book);

        return savedRecord;
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        if (record.getStatus() == 2) {
            throw new RuntimeException("图书已归还");
        }

        // 标记为已归还
        record.markAsReturned();
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        // 更新图书的可借数量
        Book book = record.getBook();
        if (book != null) {
            book.setBorrowedCopies(Math.max(0, book.getBorrowedCopies() - 1));
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            book.setUpdateTime(LocalDateTime.now());
            bookRepository.save(book);
        }

        return savedRecord;
    }

    @Override
    @Transactional
    public BorrowRecord renewBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        if (record.getStatus() != 1) {
            throw new RuntimeException("只有借阅中的图书可以续借");
        }

        if (record.getRenewCount() >= 3) {
            throw new RuntimeException("续借次数已达上限（最多3次）");
        }

        record.renew();
        return borrowRecordRepository.save(record);
    }

    @Override
    public Optional<BorrowRecord> findById(Long id) {
        return borrowRecordRepository.findById(id);
    }

    @Override
    public Page<BorrowRecord> getUserRecords(Long userId, Pageable pageable) {
        return borrowRecordRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<BorrowRecord> getCurrentBorrowsByUser(Long userId) {
        return borrowRecordRepository.findCurrentBorrowsByUser(userId);
    }

    @Override
    public Page<BorrowRecord> findAll(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable);
    }

    @Override
    public Page<BorrowRecord> findByStatus(Integer status, Pageable pageable) {
        return borrowRecordRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<BorrowRecord> searchRecords(String username, String bookTitle, Pageable pageable) {
        return borrowRecordRepository.searchRecords(username, bookTitle, pageable);
    }

    @Override
    public Page<BorrowRecord> getOverdueRecords(Pageable pageable) {
        return borrowRecordRepository.findOverdueRecords(LocalDateTime.now(), pageable);
    }

    @Override
    public List<BorrowRecord> getDueSoonRecords() {
        LocalDateTime threeDaysLater = LocalDateTime.now().plusDays(3);
        return borrowRecordRepository.findDueSoonRecords(LocalDateTime.now(), threeDaysLater);
    }

    @Override
    public boolean canUserBorrow(Long userId) {
        // 检查用户是否存在且状态正常
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getStatus() != 1) {
            return false;
        }

        // 检查当前借阅数量是否超过限制
        long currentBorrows = borrowRecordRepository.countCurrentBorrowsByUser(userId);
        return currentBorrows < MAX_BORROW_LIMIT;
    }

    @Override
    public boolean isBookAvailable(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (!bookOpt.isPresent()) {
            return false;
        }

        Book book = bookOpt.get();
        return book.getStatus() == 1 && book.getAvailableCopies() > 0;
    }

    @Override
    public BorrowStatistics getStatistics() {
        long totalRecords = borrowRecordRepository.count();
        long currentBorrows = borrowRecordRepository.countRecordsByStatus().stream()
                .filter(result -> result[0].equals(1))
                .mapToLong(result -> (Long) result[1])
                .sum();
        long overdueRecords = borrowRecordRepository.countOverdueRecords(LocalDateTime.now());
        long returnedRecords = borrowRecordRepository.countRecordsByStatus().stream()
                .filter(result -> result[0].equals(2))
                .mapToLong(result -> (Long) result[1])
                .sum();
        Double totalFines = borrowRecordRepository.sumTotalFines();

        return new BorrowStatistics(totalRecords, currentBorrows, overdueRecords,
                returnedRecords, totalFines != null ? totalFines : 0.0);
    }

    @Override
    public Page<BorrowRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return borrowRecordRepository.findRecordsByDateRange(startDate, endDate, pageable);
    }

    @Override
    public UserBorrowStats getUserStats(Long userId) {
        long totalBorrows = borrowRecordRepository.countTotalBorrowsByUser(userId);
        long currentBorrows = borrowRecordRepository.countCurrentBorrowsByUser(userId);

        // 计算逾期数量和总罚金
        List<BorrowRecord> userRecords = borrowRecordRepository.findCurrentBorrowsByUser(userId);
        long overdueCount = userRecords.stream()
                .filter(record -> record.isOverdue())
                .count();

        double totalFines = userRecords.stream()
                .filter(record -> record.getFineAmount() != null)
                .mapToDouble(record -> record.getFineAmount())
                .sum();

        return new UserBorrowStats(totalBorrows, currentBorrows, overdueCount, totalFines);
    }

    @Override
    @Transactional
    public List<BorrowRecord> returnMultipleBooks(List<Long> recordIds) {
        List<BorrowRecord> returnedRecords = new ArrayList<>();
        for (Long recordId : recordIds) {
            try {
                BorrowRecord returned = returnBook(recordId);
                returnedRecords.add(returned);
            } catch (RuntimeException e) {
                // 记录失败但不影响其他记录的归还
                System.err.println("归还记录失败 ID: " + recordId + ", 错误: " + e.getMessage());
            }
        }
        return returnedRecords;
    }

    @Override
    @Transactional
    public List<BorrowRecord> renewMultipleBooks(List<Long> recordIds) {
        List<BorrowRecord> renewedRecords = new ArrayList<>();
        for (Long recordId : recordIds) {
            try {
                BorrowRecord renewed = renewBook(recordId);
                renewedRecords.add(renewed);
            } catch (RuntimeException e) {
                // 记录失败但不影响其他记录的续借
                System.err.println("续借记录失败 ID: " + recordId + ", 错误: " + e.getMessage());
            }
        }
        return renewedRecords;
    }

    @Override
    @Transactional
    public void updateOverdueStatus() {
        // 获取所有借阅中的记录
        List<BorrowRecord> currentRecords = borrowRecordRepository.findByStatus(1, null)
                .getContent();

        LocalDateTime now = LocalDateTime.now();
        for (BorrowRecord record : currentRecords) {
            if (now.isAfter(record.getDueDate())) {
                record.setStatus(3); // 标记为逾期
                record.setUpdateTime(now);
                borrowRecordRepository.save(record);
            }
        }
    }

    @Override
    public List<PopularBookStats> getPopularBooks(Pageable pageable) {
        List<Object[]> results = borrowRecordRepository.findMostPopularBooks(pageable);
        return results.stream()
                .map(result -> new PopularBookStats((String) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }
}