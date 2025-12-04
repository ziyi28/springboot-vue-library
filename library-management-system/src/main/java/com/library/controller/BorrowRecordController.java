package com.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.service.BorrowRecordService;
import com.library.service.UserService;
import com.library.utils.JwtUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    @Resource
    private BorrowRecordService borrowRecordService;

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<BorrowRecord>> getBorrowList(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestParam(required = false) Long userId,
                                                  @RequestParam(required = false) Integer status) {
        Page<BorrowRecord> page = new Page<>(current, size);
        Page<BorrowRecord> recordPage = borrowRecordService.getBorrowRecordsWithDetails(page, userId, status);
        return Result.success(recordPage);
    }

    @GetMapping("/my-records")
    public Result<Page<BorrowRecord>> getMyBorrowRecords(@RequestParam(defaultValue = "1") Integer current,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestParam(required = false) Integer status,
                                                        HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }

        Page<BorrowRecord> page = new Page<>(current, size);
        Page<BorrowRecord> recordPage = borrowRecordService.getBorrowRecordsWithDetails(page, userId, status);
        return Result.success(recordPage);
    }

    @PostMapping("/borrow/{bookId}")
    public Result<String> borrowBook(@PathVariable Long bookId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }

        return borrowRecordService.borrowBook(userId, bookId) ?
               Result.success("借阅成功") : Result.error("借阅失败，请检查图书是否可借或您是否已借阅该书");
    }

    @PostMapping("/return/{recordId}")
    public Result<String> returnBook(@PathVariable Long recordId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }

        return borrowRecordService.returnBook(userId, recordId) ?
               Result.success("归还成功") : Result.error("归还失败，请检查借阅记录是否存在");
    }

    @PostMapping("/renew/{recordId}")
    public Result<String> renewBook(@PathVariable Long recordId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }

        return borrowRecordService.renewBook(userId, recordId) ?
               Result.success("续借成功") : Result.error("续借失败，请检查是否超过续借次数限制");
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> getOverdueRecords() {
        return Result.success(borrowRecordService.getOverdueRecords());
    }

    @PostMapping("/update-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> updateOverdueRecords() {
        return borrowRecordService.updateOverdueRecords() ?
               Result.success("逾期记录更新成功") : Result.error("逾期记录更新失败");
    }

    @Data
    public static class BorrowRequest {
        private Long bookId;
        private Integer days; // 借阅天数
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtils.getUsernameFromToken(token);
            if (username != null) {
                User user = userService.findByUsername(username);
                return user != null ? user.getId() : null;
            }
        }
        return null;
    }
}