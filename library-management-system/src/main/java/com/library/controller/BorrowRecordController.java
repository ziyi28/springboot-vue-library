package com.library.controller;

import com.library.model.BorrowRecord;
import com.library.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/borrow-records")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 借阅图书
     */
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            BorrowRecord record = borrowRecordService.borrowBook(userId, bookId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图书借阅成功");
            response.put("data", record);
            return ResponseEntity.ok(response);
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
    @PostMapping("/{recordId}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowRecordService.returnBook(recordId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图书归还成功");
            response.put("data", record);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 续借图书
     */
    @PostMapping("/{recordId}/renew")
    public ResponseEntity<?> renewBook(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowRecordService.renewBook(recordId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "图书续借成功");
            response.put("data", record);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量归还图书
     */
    @PostMapping("/batch-return")
    public ResponseEntity<?> batchReturnBooks(@RequestBody List<Long> recordIds) {
        try {
            List<BorrowRecord> returnedRecords = borrowRecordService.returnMultipleBooks(recordIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量归还成功");
            response.put("data", returnedRecords);
            response.put("count", returnedRecords.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量续借图书
     */
    @PostMapping("/batch-renew")
    public ResponseEntity<?> batchRenewBooks(@RequestBody List<Long> recordIds) {
        try {
            List<BorrowRecord> renewedRecords = borrowRecordService.renewMultipleBooks(recordIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量续借成功");
            response.put("data", renewedRecords);
            response.put("count", renewedRecords.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取借阅记录详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBorrowRecordById(@PathVariable Long id) {
        Optional<BorrowRecord> record = borrowRecordService.findById(id);
        if (record.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", record.get());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "借阅记录不存在");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取用户借阅记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBorrowRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<BorrowRecord> records = borrowRecordService.getUserRecords(userId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records.getContent());
        response.put("currentPage", records.getNumber());
        response.put("totalItems", records.getTotalElements());
        response.put("totalPages", records.getTotalPages());
        response.put("pageSize", records.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户当前借阅
     */
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<?> getCurrentBorrowsByUser(@PathVariable Long userId) {
        List<BorrowRecord> records = borrowRecordService.getCurrentBorrowsByUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        response.put("count", records.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有借阅记录（分页）
     */
    @GetMapping
    public ResponseEntity<?> getAllBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<BorrowRecord> records = borrowRecordService.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records.getContent());
        response.put("currentPage", records.getNumber());
        response.put("totalItems", records.getTotalElements());
        response.put("totalPages", records.getTotalPages());
        response.put("pageSize", records.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 根据状态获取借阅记录
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getBorrowRecordsByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowDate"));
        Page<BorrowRecord> records = borrowRecordService.findByStatus(status, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records.getContent());
        response.put("currentPage", records.getNumber());
        response.put("totalItems", records.getTotalElements());
        response.put("totalPages", records.getTotalPages());
        response.put("pageSize", records.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 搜索借阅记录
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchBorrowRecords(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowDate"));
        Page<BorrowRecord> records = borrowRecordService.searchRecords(username, bookTitle, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records.getContent());
        response.put("currentPage", records.getNumber());
        response.put("totalItems", records.getTotalElements());
        response.put("totalPages", records.getTotalPages());
        response.put("pageSize", records.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取逾期记录
     */
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        Page<BorrowRecord> records = borrowRecordService.getOverdueRecords(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records.getContent());
        response.put("currentPage", records.getNumber());
        response.put("totalItems", records.getTotalElements());
        response.put("totalPages", records.getTotalPages());
        response.put("pageSize", records.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取即将到期记录
     */
    @GetMapping("/due-soon")
    public ResponseEntity<?> getDueSoonRecords() {
        List<BorrowRecord> records = borrowRecordService.getDueSoonRecords();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        response.put("count", records.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 根据日期范围获取借阅记录
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getRecordsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowDate"));
        Page<BorrowRecord> records = borrowRecordService.findByDateRange(start, end, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records.getContent());
        response.put("currentPage", records.getNumber());
        response.put("totalItems", records.getTotalElements());
        response.put("totalPages", records.getTotalPages());
        response.put("pageSize", records.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * 检查用户是否可以借阅
     */
    @GetMapping("/check-borrow-permission/{userId}")
    public ResponseEntity<?> checkBorrowPermission(@PathVariable Long userId) {
        boolean canBorrow = borrowRecordService.canUserBorrow(userId);
        boolean isAvailable = false;

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "canBorrow", canBorrow,
            "maxLimit", 5,
            "message", canBorrow ? "可以借阅" : "已达到借阅上限或权限受限"
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * 检查图书是否可借
     */
    @GetMapping("/check-book-availability/{bookId}")
    public ResponseEntity<?> checkBookAvailability(@PathVariable Long bookId) {
        boolean isAvailable = borrowRecordService.isBookAvailable(bookId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "bookId", bookId,
            "isAvailable", isAvailable,
            "message", isAvailable ? "图书可借" : "图书无可借副本"
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * 获取借阅统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getBorrowStatistics() {
        BorrowRecordService.BorrowStatistics statistics = borrowRecordService.getStatistics();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "totalRecords", statistics.getTotalRecords(),
            "currentBorrows", statistics.getCurrentBorrows(),
            "overdueRecords", statistics.getOverdueRecords(),
            "returnedRecords", statistics.getReturnedRecords(),
            "totalFines", statistics.getTotalFines()
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户借阅统计
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<?> getUserBorrowStatistics(@PathVariable Long userId) {
        BorrowRecordService.UserBorrowStats stats = borrowRecordService.getUserStats(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "totalBorrows", stats.getTotalBorrows(),
            "currentBorrows", stats.getCurrentBorrows(),
            "overdueCount", stats.getOverdueCount(),
            "totalFines", stats.getTotalFines()
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * 获取热门图书统计
     */
    @GetMapping("/popular-books")
    public ResponseEntity<?> getPopularBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<BorrowRecordService.PopularBookStats> popularBooks = borrowRecordService.getPopularBooks(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", popularBooks);

        return ResponseEntity.ok(response);
    }

    /**
     * 更新逾期状态
     */
    @PostMapping("/update-overdue")
    public ResponseEntity<?> updateOverdueStatus() {
        borrowRecordService.updateOverdueStatus();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "逾期状态更新成功");

        return ResponseEntity.ok(response);
    }
}