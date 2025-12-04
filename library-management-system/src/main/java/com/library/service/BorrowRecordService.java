package com.library.service;

import com.library.model.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordService {

    // 借阅图书
    BorrowRecord borrowBook(Long userId, Long bookId);

    // 归还图书
    BorrowRecord returnBook(Long recordId);

    // 续借图书
    BorrowRecord renewBook(Long recordId);

    // 获取借阅记录详情
    Optional<BorrowRecord> findById(Long id);

    // 获取用户借阅记录
    Page<BorrowRecord> getUserRecords(Long userId, Pageable pageable);

    // 获取用户当前借阅
    List<BorrowRecord> getCurrentBorrowsByUser(Long userId);

    // 获取所有借阅记录
    Page<BorrowRecord> findAll(Pageable pageable);

    // 根据状态获取借阅记录
    Page<BorrowRecord> findByStatus(Integer status, Pageable pageable);

    // 搜索借阅记录
    Page<BorrowRecord> searchRecords(String username, String bookTitle, Pageable pageable);

    // 获取逾期记录
    Page<BorrowRecord> getOverdueRecords(Pageable pageable);

    // 获取即将到期记录
    List<BorrowRecord> getDueSoonRecords();

    // 检查用户是否可以借阅（借阅限制等）
    boolean canUserBorrow(Long userId);

    // 检查图书是否可借
    boolean isBookAvailable(Long bookId);

    // 获取借阅统计信息
    BorrowStatistics getStatistics();

    // 根据日期范围获取借阅记录
    Page<BorrowRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 获取用户借阅统计
    UserBorrowStats getUserStats(Long userId);

    // 批量归还图书
    List<BorrowRecord> returnMultipleBooks(List<Long> recordIds);

    // 批量续借图书
    List<BorrowRecord> renewMultipleBooks(List<Long> recordIds);

    // 自动更新逾期状态
    void updateOverdueStatus();

    // 获取最受欢迎图书
    List<PopularBookStats> getPopularBooks(Pageable pageable);

    // 借阅统计信息类
    class BorrowStatistics {
        private long totalRecords;
        private long currentBorrows;
        private long overdueRecords;
        private long returnedRecords;
        private double totalFines;

        public BorrowStatistics(long totalRecords, long currentBorrows, long overdueRecords,
                               long returnedRecords, double totalFines) {
            this.totalRecords = totalRecords;
            this.currentBorrows = currentBorrows;
            this.overdueRecords = overdueRecords;
            this.returnedRecords = returnedRecords;
            this.totalFines = totalFines;
        }

        // Getters
        public long getTotalRecords() { return totalRecords; }
        public long getCurrentBorrows() { return currentBorrows; }
        public long getOverdueRecords() { return overdueRecords; }
        public long getReturnedRecords() { return returnedRecords; }
        public double getTotalFines() { return totalFines; }
    }

    // 用户借阅统计类
    class UserBorrowStats {
        private long totalBorrows;
        private long currentBorrows;
        private long overdueCount;
        private double totalFines;

        public UserBorrowStats(long totalBorrows, long currentBorrows, long overdueCount, double totalFines) {
            this.totalBorrows = totalBorrows;
            this.currentBorrows = currentBorrows;
            this.overdueCount = overdueCount;
            this.totalFines = totalFines;
        }

        // Getters
        public long getTotalBorrows() { return totalBorrows; }
        public long getCurrentBorrows() { return currentBorrows; }
        public long getOverdueCount() { return overdueCount; }
        public double getTotalFines() { return totalFines; }
    }

    // 热门图书统计类
    class PopularBookStats {
        private String bookTitle;
        private long borrowCount;

        public PopularBookStats(String bookTitle, long borrowCount) {
            this.bookTitle = bookTitle;
            this.borrowCount = borrowCount;
        }

        // Getters
        public String getBookTitle() { return bookTitle; }
        public long getBorrowCount() { return borrowCount; }
    }
}