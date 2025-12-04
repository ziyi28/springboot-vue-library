package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 添加图书
     */
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图书添加成功");
            response.put("data", savedBook);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新图书信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        try {
            Book updatedBook = bookService.updateBook(book);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图书信息更新成功");
            response.put("data", updatedBook);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            boolean deleted = bookService.deleteBook(id);
            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "图书删除成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "图书不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID查找图书
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.findById(id);
        if (book.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", book.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "图书不存在");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据ISBN查找图书
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.findByIsbn(isbn);
        if (book.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", book.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "图书不存在");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取所有图书（分页）
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Book> books = bookService.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", books.getContent());
        response.put("currentPage", books.getNumber());
        response.put("totalItems", books.getTotalElements());
        response.put("totalPages", books.getTotalPages());
        response.put("pageSize", books.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 搜索图书
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooks(keyword, status, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", books.getContent());
        response.put("currentPage", books.getNumber());
        response.put("totalItems", books.getTotalElements());
        response.put("totalPages", books.getTotalPages());
        response.put("pageSize", books.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取可借阅图书
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBooks() {
        List<Book> books = bookService.getAvailableBooks();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", books);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取热门图书
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Book> books = bookService.getPopularBooks(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", books);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取新书上架
     */
    @GetMapping("/new")
    public ResponseEntity<?> getNewBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Book> books = bookService.getNewBooks(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", books);

        return ResponseEntity.ok(response);
    }

    /**
     * 借阅图书
     */
    @PostMapping("/{id}/borrow")
    public ResponseEntity<?> borrowBook(@PathVariable Long id) {
        try {
            boolean success = bookService.borrowBook(id);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "图书借阅成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "图书借阅失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 归还图书
     */
    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        try {
            boolean success = bookService.returnBook(id);
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "图书归还成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "图书归还失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取图书统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        BookService.BookStatistics statistics = bookService.getStatistics();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "totalBooks", statistics.getTotalBooks(),
            "availableBooks", statistics.getAvailableBooks(),
            "borrowedBooks", statistics.getBorrowedBooks(),
            "categoryCounts", statistics.getCategoryCounts()
        ));

        return ResponseEntity.ok(response);
    }
}