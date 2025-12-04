package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Resource
    private BookMapper bookMapper;

    @Override
    public Page<Book> getBookPage(Page<Book> page, String title, String author, Long categoryId) {
        return bookMapper.selectBooksWithCategory(page, title, author, categoryId);
    }

    @Override
    public boolean addBook(Book book) {
        book.setAvailableCopies(book.getTotalCopies());
        book.setStatus(1);
        return this.save(book);
    }

    @Override
    public boolean updateAvailableCopies(Long bookId, int change) {
        Book book = this.getById(bookId);
        if (book != null) {
            int newAvailable = book.getAvailableCopies() + change;
            if (newAvailable >= 0 && newAvailable <= book.getTotalCopies()) {
                book.setAvailableCopies(newAvailable);
                return this.updateById(book);
            }
        }
        return false;
    }

    @Override
    public List<Book> getPopularBooks(int limit) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("total_copies");
        wrapper.last("LIMIT " + limit);
        wrapper.eq("status", 1);
        return this.list(wrapper);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("title", keyword)
                    .or().like("author", keyword)
                    .or().like("isbn", keyword));
        }
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public Book getBookWithCategory(Long bookId) {
        return bookMapper.selectBooksWithCategory(new Page<>(1, 1), null, null, null)
                .getRecords().stream()
                .filter(book -> book.getId().equals(bookId))
                .findFirst()
                .orElse(null);
    }
}