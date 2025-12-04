package com.library.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BorrowRecord;

import java.util.List;

public interface BorrowRecordService extends IService<BorrowRecord> {

    Page<BorrowRecord> getBorrowRecordsWithDetails(Page<BorrowRecord> page, Long userId, Integer status);

    boolean borrowBook(Long userId, Long bookId);

    boolean returnBook(Long userId, Long recordId);

    boolean renewBook(Long userId, Long recordId);

    List<BorrowRecord> getOverdueRecords();

    boolean updateOverdueRecords();
}