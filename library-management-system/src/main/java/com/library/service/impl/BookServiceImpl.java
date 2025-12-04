package com.library.service.impl;

import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book addBook(Book book) {
        // 检查ISBN是否已存在
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("ISBN已存在");
        }

        // 设置默认值
        if (book.getTotalCopies() == null || book.getTotalCopies() <= 0) {
            book.setTotalCopies(1);
        }

        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        if (book.getBorrowedCopies() == null) {
            book.setBorrowedCopies(0);
        }

        if (book.getStatus() == null) {
            book.setStatus(1); // 默认状态：可用
        }

        // 验证数据一致性
        if (book.getAvailableCopies() + book.getBorrowedCopies() > book.getTotalCopies()) {
            throw new RuntimeException("可借数量和已借数量之和不能超过总数量");
        }

        // 设置时间
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());

        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        // 如果要修改ISBN，检查是否与其他图书冲突
        if (book.getIsbn() != null && !book.getIsbn().equals(existingBook.getIsbn())) {
            if (bookRepository.existsByIsbn(book.getIsbn())) {
                throw new RuntimeException("ISBN已被其他图书使用");
            }
            existingBook.setIsbn(book.getIsbn());
        }

        // 更新允许修改的字段
        if (book.getTitle() != null) {
            existingBook.setTitle(book.getTitle());
        }
        if (book.getAuthor() != null) {
            existingBook.setAuthor(book.getAuthor());
        }
        if (book.getPublisher() != null) {
            existingBook.setPublisher(book.getPublisher());
        }
        if (book.getPublishDate() != null) {
            existingBook.setPublishDate(book.getPublishDate());
        }
        if (book.getPrice() != null) {
            existingBook.setPrice(book.getPrice());
        }
        if (book.getCategory() != null) {
            existingBook.setCategory(book.getCategory());
        }
        if (book.getDescription() != null) {
            existingBook.setDescription(book.getDescription());
        }
        if (book.getCoverImage() != null) {
            existingBook.setCoverImage(book.getCoverImage());
        }

        // 更新数量信息时保持数据一致性
        if (book.getTotalCopies() != null && book.getTotalCopies() > 0) {
            int oldTotal = existingBook.getTotalCopies();
            int newTotal = book.getTotalCopies();
            int diff = newTotal - oldTotal;

            existingBook.setTotalCopies(newTotal);
            // 如果总数量增加，相应增加可借数量
            if (diff > 0) {
                existingBook.setAvailableCopies(existingBook.getAvailableCopies() + diff);
            }
        }

        if (book.getStatus() != null) {
            existingBook.setStatus(book.getStatus());
        }

        existingBook.setUpdateTime(LocalDateTime.now());

        return bookRepository.save(existingBook);
    }

    @Override
    public boolean deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            return false;
        }

        Book book = bookRepository.findById(id).get();

        // 检查是否有借出的副本
        if (book.getBorrowedCopies() > 0) {
            throw new RuntimeException("图书有借出的副本，无法删除");
        }

        bookRepository.deleteById(id);
        return true;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Integer status, Pageable pageable) {
        return bookRepository.searchBooks(keyword, status, pageable);
    }

    @Override
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }

    @Override
    public List<Book> getPopularBooks(Pageable pageable) {
        return bookRepository.findPopularBooks(pageable);
    }

    @Override
    public List<Book> getNewBooks(Pageable pageable) {
        return bookRepository.findNewBooks(pageable);
    }

    @Override
    public boolean borrowBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        if (book.getStatus() != 1) {
            throw new RuntimeException("图书不可用");
        }

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("没有可借阅的副本");
        }

        // 更新数量
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setBorrowedCopies(book.getBorrowedCopies() + 1);
        book.setUpdateTime(LocalDateTime.now());

        bookRepository.save(book);
        return true;
    }

    @Override
    public boolean returnBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));

        if (book.getBorrowedCopies() <= 0) {
            throw new RuntimeException("没有借出的副本");
        }

        // 更新数量
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setBorrowedCopies(book.getBorrowedCopies() - 1);
        book.setUpdateTime(LocalDateTime.now());

        bookRepository.save(book);
        return true;
    }

    @Override
    public BookService.BookStatistics getStatistics() {
        long totalBooks = bookRepository.countActiveBooks();
        long availableBooks = bookRepository.countAvailableBooks();
        long borrowedBooks = bookRepository.countBorrowedBooks();

        List<Object[]> categoryResults = bookRepository.countBooksByCategory();
        List<BookService.BookStatistics.CategoryCount> categoryCounts = categoryResults.stream()
                .map(result -> new BookService.BookStatistics.CategoryCount(
                        (String) result[0],
                        (Long) result[1]
                ))
                .collect(Collectors.toList());

        return new BookService.BookStatistics(totalBooks, availableBooks, borrowedBooks, categoryCounts);
    }
}