package com.library.repository;

import com.library.model.BookRecord;
import com.library.model.BookRecord.CopyStatus;
import com.library.model.BookRecord.ConditionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {

    // 根据条码号查找
    Optional<BookRecord> findByBarcode(String barcode);

    // 根据索书号查找
    Optional<BookRecord> findByCallNumber(String callNumber);

    // 根据图书ID查找所有副本
    List<BookRecord> findByBookId(Long bookId);

    // 根据状态查找副本
    List<BookRecord> findByStatus(CopyStatus status);

    // 根据图书ID和状态查找副本
    List<BookRecord> findByBookIdAndStatus(Long bookId, CopyStatus status);

    // 根据馆藏位置查找副本
    List<BookRecord> findByLocationContainingIgnoreCase(String location);

    // 根据状态类型查找副本
    List<BookRecord> findByCondition(ConditionType condition);

    // 检查条码号是否存在
    boolean existsByBarcode(String barcode);

    // 检查索书号是否存在
    boolean existsByCallNumber(String callNumber);

    // 统计不同状态的副本数量
    @Query("SELECT r.status, COUNT(r) FROM BookRecord r GROUP BY r.status")
    List<Object[]> countByStatus();

    // 统计不同状况类型的副本数量
    @Query("SELECT r.condition, COUNT(r) FROM BookRecord r GROUP BY r.condition")
    List<Object[]> countByCondition();

    // 统计某本图书的副本数量（按状态）
    @Query("SELECT r.status, COUNT(r) FROM BookRecord r WHERE r.book.id = :bookId GROUP BY r.status")
    List<Object[]> countByBookIdAndStatus(@Param("bookId") Long bookId);

    // 查找最近借出的副本
    @Query("SELECT r FROM BookRecord r WHERE r.lastBorrowDate IS NOT NULL ORDER BY r.lastBorrowDate DESC")
    List<BookRecord> findRecentlyBorrowed();

    // 查找最近购买的副本
    @Query("SELECT r FROM BookRecord r WHERE r.purchaseDate IS NOT NULL ORDER BY r.purchaseDate DESC")
    List<BookRecord> findRecentlyPurchased();

    // 查找预期归还时间在指定日期之前的副本（逾期检测）
    @Query("SELECT r FROM BookRecord r WHERE r.expectedReturnDate < :date AND r.status = 'BORROWED'")
    List<BookRecord> findOverdueCopies(@Param("date") LocalDateTime date);

    // 模糊搜索副本（条码号、索书号、位置）
    @Query("SELECT r FROM BookRecord r WHERE " +
           "LOWER(r.barcode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.callNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<BookRecord> searchRecords(@Param("keyword") String keyword);

    // 根据价格范围查找副本
    @Query("SELECT r FROM BookRecord r WHERE r.purchasePrice BETWEEN :minPrice AND :maxPrice")
    List<BookRecord> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // 查找没有索书号的副本
    List<BookRecord> findByCallNumberIsNull();

    // 查找没有位置的副本
    List<BookRecord> findByLocationIsNull();

    // 统计各位置的副本数量
    @Query("SELECT r.location, COUNT(r) FROM BookRecord r WHERE r.location IS NOT NULL GROUP BY r.location")
    List<Object[]> countByLocation();

    // 查找购买时间在指定范围内的副本
    @Query("SELECT r FROM BookRecord r WHERE r.purchaseDate BETWEEN :startDate AND :endDate")
    List<BookRecord> findByPurchaseDateBetween(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // 根据图书ID统计副本总数
    @Query("SELECT COUNT(r) FROM BookRecord r WHERE r.book.id = :bookId")
    Long countByBookId(@Param("bookId") Long bookId);

    // 根据图书ID统计可用副本数
    @Query("SELECT COUNT(r) FROM BookRecord r WHERE r.book.id = :bookId AND r.status = 'AVAILABLE'")
    Long countAvailableByBookId(@Param("bookId") Long bookId);

    // 根据图书ID统计已借出副本数
    @Query("SELECT COUNT(r) FROM BookRecord r WHERE r.book.id = :bookId AND r.status = 'BORROWED'")
    Long countBorrowedByBookId(@Param("bookId") Long bookId);

    // 查找状态为可用且没有位置的副本
    @Query("SELECT r FROM BookRecord r WHERE r.status = 'AVAILABLE' AND (r.location IS NULL OR r.location = '')")
    List<BookRecord> findAvailableWithoutLocation();
}