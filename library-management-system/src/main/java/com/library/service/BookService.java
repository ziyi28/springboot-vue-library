package com.library.service;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {

    // 添加图书
    Book addBook(Book book);

    // 更新图书信息
    Book updateBook(Book book);

    // 删除图书
    boolean deleteBook(Long id);

    // 根据ID查找图书
    Optional<Book> findById(Long id);

    // 根据ISBN查找图书
    Optional<Book> findByIsbn(String isbn);

    // 获取所有图书（分页）
    Page<Book> findAll(Pageable pageable);

    // 搜索图书
    Page<Book> searchBooks(String keyword, Integer status, Pageable pageable);

    // 获取可借阅图书
    List<Book> getAvailableBooks();

    // 获取热门图书
    List<Book> getPopularBooks(Pageable pageable);

    // 获取新书
    List<Book> getNewBooks(Pageable pageable);

    // 借阅图书
    boolean borrowBook(Long bookId);

    // 归还图书
    boolean returnBook(Long bookId);

    // 获取图书统计信息
    BookStatistics getStatistics();

    // 图书统计信息内部类
    class BookStatistics {
        private long totalBooks;
        private long availableBooks;
        private long borrowedBooks;
        private List<CategoryCount> categoryCounts;

        // 构造函数
        public BookStatistics(long totalBooks, long availableBooks, long borrowedBooks, List<CategoryCount> categoryCounts) {
            this.totalBooks = totalBooks;
            this.availableBooks = availableBooks;
            this.borrowedBooks = borrowedBooks;
            this.categoryCounts = categoryCounts;
        }

        // Getters and Setters
        public long getTotalBooks() { return totalBooks; }
        public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }

        public long getAvailableBooks() { return availableBooks; }
        public void setAvailableBooks(long availableBooks) { this.availableBooks = availableBooks; }

        public long getBorrowedBooks() { return borrowedBooks; }
        public void setBorrowedBooks(long borrowedBooks) { this.borrowedBooks = borrowedBooks; }

        public List<CategoryCount> getCategoryCounts() { return categoryCounts; }
        public void setCategoryCounts(List<CategoryCount> categoryCounts) { this.categoryCounts = categoryCounts; }

        // 分类统计内部类
        public static class CategoryCount {
            private String category;
            private long count;

            public CategoryCount(String category, long count) {
                this.category = category;
                this.count = count;
            }

            public String getCategory() { return category; }
            public void setCategory(String category) { this.category = category; }

            public long getCount() { return count; }
            public void setCount(long count) { this.count = count; }
        }
    }
}