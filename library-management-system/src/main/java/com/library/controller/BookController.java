package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 添加图书
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Book>> addBook(
            @RequestBody Book book,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Book savedBook = bookService.addBook(book);
            return ResponseEntity.ok(ApiResponse.success("图书添加成功", savedBook));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest("添加图书失败: " + e.getMessage()));
        }
    }

    /**
     * 更新图书信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> updateBook(
            @PathVariable Long id,
            @RequestBody Book book,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        book.setId(id);
        try {
            Book updatedBook = bookService.updateBook(book);
            return ResponseEntity.ok(ApiResponse.success("图书信息更新成功", updatedBook));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest("更新图书失败: " + e.getMessage()));
        }
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBook(
            @PathVariable Long id,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            boolean deleted = bookService.deleteBook(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("图书删除成功", "删除成功"));
            } else {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error("图书不存在"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 根据ID查找图书
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.findById(id);
        if (book.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("获取图书成功", book.get()));
        } else {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("图书不存在"));
        }
    }

    /**
     * 根据ISBN查找图书
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<Book>> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.findByIsbn(isbn);
        if (book.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("获取图书成功", book.get()));
        } else {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("图书不存在"));
        }
    }

    /**
     * 获取所有图书（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Book>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Book> books = bookService.findAll(pageable);
            return ResponseEntity.ok(ApiResponse.success("获取图书列表成功", books));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取图书列表失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索图书
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Book>>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooks(keyword, status, pageable);

        return ResponseEntity.ok(ApiResponse.success("搜索图书成功", books));
    }

    /**
     * 获取可借阅图书
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Book>>> getAvailableBooks() {
        List<Book> books = bookService.getAvailableBooks();
        return ResponseEntity.ok(ApiResponse.success("获取可借阅图书成功", books));
    }

    /**
     * 获取热门图书
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<Book>>> getPopularBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Book> books = bookService.getPopularBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success("获取热门图书成功", books));
    }

    /**
     * 获取新书上架
     */
    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<Book>>> getNewBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Book> books = bookService.getNewBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success("获取新书上架成功", books));
    }

    /**
     * 借阅图书
     */
    @PostMapping("/{id}/borrow")
    public ResponseEntity<ApiResponse<String>> borrowBook(@PathVariable Long id) {
        try {
            boolean success = bookService.borrowBook(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("图书借阅成功", "借阅成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("图书借阅失败"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 归还图书
     */
    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<String>> returnBook(@PathVariable Long id) {
        try {
            boolean success = bookService.returnBook(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("图书归还成功", "归还成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("图书归还失败"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 获取图书统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getStatistics() {
        BookService.BookStatistics statistics = bookService.getStatistics();

        return ResponseEntity.ok(ApiResponse.success("获取图书统计成功",
            java.util.Map.of(
                "totalBooks", statistics.getTotalBooks(),
                "availableBooks", statistics.getAvailableBooks(),
                "borrowedBooks", statistics.getBorrowedBooks(),
                "categoryCounts", statistics.getCategoryCounts()
            )));
    }
}