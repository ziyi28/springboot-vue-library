package com.library.repository;

import com.library.model.BorrowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // 根据用户ID查找借阅记录
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    // 根据用户ID和状态查找借阅记录
    Page<BorrowRecord> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    // 根据图书ID查找借阅记录
    Page<BorrowRecord> findByBookId(Long bookId, Pageable pageable);

    // 根据状态查找借阅记录
    Page<BorrowRecord> findByStatus(Integer status, Pageable pageable);

    // 查找逾期未还的记录
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 1 AND br.dueDate < :now")
    Page<BorrowRecord> findOverdueRecords(@Param("now") LocalDateTime now, Pageable pageable);

    // 根据用户名、书名搜索借阅记录
    @Query("SELECT br FROM BorrowRecord br WHERE " +
           "(:username IS NULL OR br.user.username LIKE %:username%) AND " +
           "(:bookTitle IS NULL OR br.book.title LIKE %:bookTitle%)")
    Page<BorrowRecord> searchRecords(@Param("username") String username,
                                    @Param("bookTitle") String bookTitle,
                                    Pageable pageable);

    // 统计用户当前借阅数量
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 1")
    long countCurrentBorrowsByUser(@Param("userId") Long userId);

    // 统计总借阅次数
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId")
    long countTotalBorrowsByUser(@Param("userId") Long userId);

    // 统计图书被借阅次数
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.book.id = :bookId")
    long countBorrowsByBook(@Param("bookId") Long bookId);

    // 查找用户当前借阅的图书
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.status = 1")
    List<BorrowRecord> findCurrentBorrowsByUser(@Param("userId") Long userId);

    // 查找图书是否被当前用户借阅
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND br.book.id = :bookId AND br.status = 1")
    BorrowRecord findCurrentBorrowByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 查找最近的借阅记录
    @Query("SELECT br FROM BorrowRecord br ORDER BY br.borrowDate DESC")
    Page<BorrowRecord> findRecentRecords(Pageable pageable);

    // 统计借阅记录状态
    @Query("SELECT br.status, COUNT(br) FROM BorrowRecord br GROUP BY br.status")
    List<Object[]> countRecordsByStatus();

    // 统计逾期记录数量
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.status = 1 AND br.dueDate < :now")
    long countOverdueRecords(@Param("now") LocalDateTime now);

    // 计算总罚金
    @Query("SELECT SUM(br.fineAmount) FROM BorrowRecord br WHERE br.fineAmount > 0")
    Double sumTotalFines();

    // 查找即将到期的借阅记录（3天内）
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 1 AND br.dueDate BETWEEN :now AND :threeDaysLater")
    List<BorrowRecord> findDueSoonRecords(@Param("now") LocalDateTime now,
                                         @Param("threeDaysLater") LocalDateTime threeDaysLater);

    // 根据日期范围查找借阅记录
    @Query("SELECT br FROM BorrowRecord br WHERE br.borrowDate BETWEEN :startDate AND :endDate")
    Page<BorrowRecord> findRecordsByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            Pageable pageable);

    // 查找用户在特定时间段的借阅记录
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.id = :userId AND " +
           "br.borrowDate BETWEEN :startDate AND :endDate")
    Page<BorrowRecord> findUserRecordsByDateRange(@Param("userId") Long userId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                Pageable pageable);

    // 统计每月借阅数量
    @Query("SELECT FUNCTION('YEAR', br.borrowDate), FUNCTION('MONTH', br.borrowDate), COUNT(br) " +
           "FROM BorrowRecord br WHERE br.borrowDate >= :startDate " +
           "GROUP BY FUNCTION('YEAR', br.borrowDate), FUNCTION('MONTH', br.borrowDate) " +
           "ORDER BY FUNCTION('YEAR', br.borrowDate), FUNCTION('MONTH', br.borrowDate)")
    List<Object[]> countMonthlyBorrows(@Param("startDate") LocalDateTime startDate);

    // 查找最受欢迎的图书
    @Query("SELECT br.book.title, COUNT(br) as borrowCount " +
           "FROM BorrowRecord br " +
           "GROUP BY br.book.title " +
           "ORDER BY borrowCount DESC")
    List<Object[]> findMostPopularBooks(Pageable pageable);
}