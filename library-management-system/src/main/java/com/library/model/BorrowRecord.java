package com.library.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "status", nullable = false)
    private Integer status; // 1: 借阅中, 2: 已归还, 3: 逾期

    @Column(name = "renew_count")
    private Integer renewCount = 0;

    @Column(name = "fine_amount")
    private Double fineAmount = 0.0;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 默认借阅期限：30天
    private static final int DEFAULT_BORROW_DAYS = 30;

    public BorrowRecord() {
        this.createTime = LocalDateTime.now();
        this.borrowDate = LocalDateTime.now();
        this.dueDate = this.borrowDate.plusDays(DEFAULT_BORROW_DAYS);
        this.status = 1; // 借阅中
    }

    public BorrowRecord(User user, Book book) {
        this();
        this.user = user;
        this.book = book;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRenewCount() {
        return renewCount;
    }

    public void setRenewCount(Integer renewCount) {
        this.renewCount = renewCount;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // 业务方法
    public boolean isOverdue() {
        return status == 1 && LocalDateTime.now().isAfter(dueDate);
    }

    public void markAsReturned() {
        this.returnDate = LocalDateTime.now();
        this.status = 2; // 已归还
        this.updateTime = LocalDateTime.now();

        // 计算罚金（如果逾期）
        if (this.returnDate.isAfter(dueDate)) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, this.returnDate);
            this.fineAmount = overdueDays * 1.0; // 每天1元罚金
        }
    }

    public void renew() {
        if (renewCount < 3 && status == 1) { // 最多续借3次
            this.dueDate = this.dueDate.plusDays(DEFAULT_BORROW_DAYS);
            this.renewCount++;
            this.updateTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();

        // 自动检查逾期状态
        if (status == 1 && LocalDateTime.now().isAfter(dueDate)) {
            this.status = 3; // 逾期
        }
    }

    // 状态描述
    public String getStatusText() {
        switch (status) {
            case 1: return "借阅中";
            case 2: return "已归还";
            case 3: return "逾期";
            default: return "未知";
        }
    }
}