package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Book;

import java.util.List;

public interface BookService extends IService<Book> {

    Page<Book> getBookPage(Page<Book> page, String title, String author, Long categoryId);

    boolean addBook(Book book);

    boolean updateAvailableCopies(Long bookId, int change);

    List<Book> getPopularBooks(int limit);

    List<Book> searchBooks(String keyword);

    Book getBookWithCategory(Long bookId);
}