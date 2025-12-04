package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 根据ISBN查找图书
    Optional<Book> findByIsbn(String isbn);

    // 检查ISBN是否存在
    boolean existsByIsbn(String isbn);

    // 根据书名模糊查询
    List<Book> findByTitleContainingIgnoreCase(String title);

    // 根据作者模糊查询
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // 根据分类查询
    List<Book> findByCategory(String category);

    // 根据状态查询
    List<Book> findByStatus(Integer status);

    // 查询有可借阅副本的图书
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 AND b.status = 1")
    List<Book> findAvailableBooks();

    // 搜索图书（支持书名、作者、分类）
    @Query("SELECT b FROM Book b WHERE " +
           "(:keyword IS NULL OR " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.category) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Book> searchBooks(@Param("keyword") String keyword, @Param("status") Integer status, Pageable pageable);

    // 获取热门图书（借阅次数最多）
    @Query("SELECT b FROM Book b WHERE b.status = 1 ORDER BY b.borrowedCopies DESC")
    List<Book> findPopularBooks(Pageable pageable);

    // 获取新书上架（按创建时间排序）
    @Query("SELECT b FROM Book b WHERE b.status = 1 ORDER BY b.createTime DESC")
    List<Book> findNewBooks(Pageable pageable);

    // 统计图书总数
    @Query("SELECT COUNT(b) FROM Book b WHERE b.status = 1")
    long countActiveBooks();

    // 统计可借阅图书数
    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0 AND b.status = 1")
    long countAvailableBooks();

    // 统计已借出图书数
    @Query("SELECT COUNT(b) FROM Book b WHERE b.borrowedCopies > 0 AND b.status = 1")
    long countBorrowedBooks();

    // 按分类统计图书数量
    @Query("SELECT b.category, COUNT(b) FROM Book b WHERE b.status = 1 GROUP BY b.category")
    List<Object[]> countBooksByCategory();
}