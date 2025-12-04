package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Resource
    private BookService bookService;

    @GetMapping("/list")
    public Result<Page<Book>> getBookList(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) String author,
                                         @RequestParam(required = false) Long categoryId) {
        Page<Book> page = new Page<>(current, size);
        Page<Book> bookPage = bookService.getBookPage(page, title, author, categoryId);
        return Result.success(bookPage);
    }

    @GetMapping("/search")
    public Result<List<Book>> searchBooks(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return Result.success(books);
    }

    @GetMapping("/popular")
    public Result<List<Book>> getPopularBooks(@RequestParam(defaultValue = "10") Integer limit) {
        List<Book> books = bookService.getPopularBooks(limit);
        return Result.success(books);
    }

    @GetMapping("/{id}")
    public Result<Book> getBookDetail(@PathVariable Long id) {
        Book book = bookService.getBookWithCategory(id);
        return book != null ? Result.success(book) : Result.error("图书不存在");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> addBook(@RequestBody Book book) {
        return bookService.addBook(book) ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return bookService.updateById(book) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> deleteBook(@PathVariable Long id) {
        return bookService.removeById(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}