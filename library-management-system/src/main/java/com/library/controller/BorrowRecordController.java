package com.library.controller;

import com.library.model.BorrowRecord;
import com.library.service.BorrowRecordService;
import com.library.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/borrow-records")
@CrossOrigin(origins = "*")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 借阅图书
     */
    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowRecord>> borrowBook(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            BorrowRecord record = borrowRecordService.borrowBook(userId, bookId);
            return ResponseEntity.ok(ApiResponse.success("图书借阅成功", record));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest("借阅失败: " + e.getMessage()));
        }
    }

    /**
     * 归还图书
     */
    @PostMapping("/{recordId}/return")
    public ResponseEntity<ApiResponse<BorrowRecord>> returnBook(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowRecordService.returnBook(recordId);
            return ResponseEntity.ok(ApiResponse.success("图书归还成功", record));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 续借图书
     */
    @PostMapping("/{recordId}/renew")
    public ResponseEntity<ApiResponse<BorrowRecord>> renewBook(@PathVariable Long recordId) {
        try {
            BorrowRecord record = borrowRecordService.renewBook(recordId);
            return ResponseEntity.ok(ApiResponse.success("图书续借成功", record));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 批量归还图书
     */
    @PostMapping("/batch-return")
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> batchReturnBooks(@RequestBody List<Long> recordIds) {
        try {
            List<BorrowRecord> returnedRecords = borrowRecordService.returnMultipleBooks(recordIds);
            return ResponseEntity.ok(ApiResponse.success("批量归还成功", returnedRecords));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 批量续借图书
     */
    @PostMapping("/batch-renew")
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> batchRenewBooks(@RequestBody List<Long> recordIds) {
        try {
            List<BorrowRecord> renewedRecords = borrowRecordService.renewMultipleBooks(recordIds);
            return ResponseEntity.ok(ApiResponse.success("批量续借成功", renewedRecords));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * 获取借阅记录详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowRecord>> getBorrowRecordById(@PathVariable Long id) {
        Optional<BorrowRecord> record = borrowRecordService.findById(id);
        if (record.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("获取借阅记录成功", record.get()));
        } else {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("借阅记录不存在"));
        }
    }

    /**
     * 获取用户借阅记录
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<BorrowRecord>>> getUserBorrowRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<BorrowRecord> records = borrowRecordService.getUserRecords(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("获取用户借阅记录成功", records));
    }

    /**
     * 获取用户当前借阅
     */
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getCurrentBorrowsByUser(@PathVariable Long userId) {
        List<BorrowRecord> records = borrowRecordService.getCurrentBorrowsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("获取用户当前借阅成功", records));
    }

    /**
     * 获取所有借阅记录（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BorrowRecord>>> getAllBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpSession session) {

        // 检查用户是否已登录
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.unauthorized("请先登录"));
        }

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<BorrowRecord> records = borrowRecordService.findAll(pageable);
            return ResponseEntity.ok(ApiResponse.success("获取借阅记录成功", records));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("获取借阅记录失败: " + e.getMessage()));
        }
    }

    /**
     * 根据状态获取借阅记录
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<BorrowRecord>>> getBorrowRecordsByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowDate"));
        Page<BorrowRecord> records = borrowRecordService.findByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("获取借阅记录成功", records));
    }

    /**
     * 搜索借阅记录
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BorrowRecord>>> searchBorrowRecords(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String bookTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowDate"));
        Page<BorrowRecord> records = borrowRecordService.searchRecords(username, bookTitle, pageable);
        return ResponseEntity.ok(ApiResponse.success("搜索借阅记录成功", records));
    }

    /**
     * 获取逾期记录
     */
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<Page<BorrowRecord>>> getOverdueRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dueDate"));
        Page<BorrowRecord> records = borrowRecordService.getOverdueRecords(pageable);
        return ResponseEntity.ok(ApiResponse.success("获取逾期记录成功", records));
    }

    /**
     * 获取即将到期记录
     */
    @GetMapping("/due-soon")
    public ResponseEntity<ApiResponse<List<BorrowRecord>>> getDueSoonRecords() {
        List<BorrowRecord> records = borrowRecordService.getDueSoonRecords();
        return ResponseEntity.ok(ApiResponse.success("获取即将到期记录成功", records));
    }

    /**
     * 根据日期范围获取借阅记录
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<Page<BorrowRecord>>> getRecordsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowDate"));
        Page<BorrowRecord> records = borrowRecordService.findByDateRange(start, end, pageable);
        return ResponseEntity.ok(ApiResponse.success("获取借阅记录成功", records));
    }

    /**
     * 检查用户是否可以借阅
     */
    @GetMapping("/check-borrow-permission/{userId}")
    public ResponseEntity<ApiResponse<Object>> checkBorrowPermission(@PathVariable Long userId) {
        boolean canBorrow = borrowRecordService.canUserBorrow(userId);

        return ResponseEntity.ok(ApiResponse.success("检查借阅权限成功",
            java.util.Map.of(
                "canBorrow", canBorrow,
                "maxLimit", 5,
                "message", canBorrow ? "可以借阅" : "已达到借阅上限或权限受限"
            )));
    }

    /**
     * 检查图书是否可借
     */
    @GetMapping("/check-book-availability/{bookId}")
    public ResponseEntity<ApiResponse<Object>> checkBookAvailability(@PathVariable Long bookId) {
        boolean isAvailable = borrowRecordService.isBookAvailable(bookId);

        return ResponseEntity.ok(ApiResponse.success("检查图书可借状态成功",
            java.util.Map.of(
                "bookId", bookId,
                "isAvailable", isAvailable,
                "message", isAvailable ? "图书可借" : "图书无可借副本"
            )));
    }

    /**
     * 获取借阅统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getBorrowStatistics() {
        BorrowRecordService.BorrowStatistics statistics = borrowRecordService.getStatistics();

        return ResponseEntity.ok(ApiResponse.success("获取借阅统计成功",
            java.util.Map.of(
                "totalRecords", statistics.getTotalRecords(),
                "currentBorrows", statistics.getCurrentBorrows(),
                "overdueRecords", statistics.getOverdueRecords(),
                "returnedRecords", statistics.getReturnedRecords(),
                "totalFines", statistics.getTotalFines()
            )));
    }

    /**
     * 获取用户借阅统计
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<ApiResponse<Object>> getUserBorrowStatistics(@PathVariable Long userId) {
        BorrowRecordService.UserBorrowStats stats = borrowRecordService.getUserStats(userId);

        return ResponseEntity.ok(ApiResponse.success("获取用户借阅统计成功",
            java.util.Map.of(
                "totalBorrows", stats.getTotalBorrows(),
                "currentBorrows", stats.getCurrentBorrows(),
                "overdueCount", stats.getOverdueCount(),
                "totalFines", stats.getTotalFines()
            )));
    }

    /**
     * 获取热门图书统计
     */
    @GetMapping("/popular-books")
    public ResponseEntity<ApiResponse<List<BorrowRecordService.PopularBookStats>>> getPopularBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<BorrowRecordService.PopularBookStats> popularBooks = borrowRecordService.getPopularBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success("获取热门图书统计成功", popularBooks));
    }

    /**
     * 更新逾期状态
     */
    @PostMapping("/update-overdue")
    public ResponseEntity<ApiResponse<String>> updateOverdueStatus() {
        borrowRecordService.updateOverdueStatus();
        return ResponseEntity.ok(ApiResponse.success("逾期状态更新成功", "更新完成"));
    }
}