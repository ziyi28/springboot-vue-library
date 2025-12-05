package com.library.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_records")
public class BookRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "barcode", nullable = false, unique = true, length = 50)
    private String barcode;

    @Column(name = "call_number", length = 20)
    private String callNumber;

    @Column(name = "location", length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CopyStatus status = CopyStatus.AVAILABLE;

    @Column(name = "condition_type")
    @Enumerated(EnumType.STRING)
    private ConditionType condition = ConditionType.GOOD;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private Double purchasePrice;

    @Column(name = "last_borrow_date")
    private LocalDateTime lastBorrowDate;

    @Column(name = "expected_return_date")
    private LocalDateTime expectedReturnDate;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public enum CopyStatus {
        AVAILABLE,    // 可借
        BORROWED,     // 已借出
        RESERVED,     // 被预约
        DAMAGED,      // 损坏
        LOST,         // 遗失
        MAINTENANCE   // 维护中
    }

    public enum ConditionType {
        EXCELLENT,    // 优秀
        GOOD,         // 良好
        FAIR,         // 一般
        POOR          // 较差
    }

    public BookRecord() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public BookRecord(Book book, String barcode) {
        this();
        this.book = book;
        this.barcode = barcode;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public void setStatus(CopyStatus status) {
        this.status = status;
    }

    public ConditionType getCondition() {
        return condition;
    }

    public void setCondition(ConditionType condition) {
        this.condition = condition;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDateTime getLastBorrowDate() {
        return lastBorrowDate;
    }

    public void setLastBorrowDate(LocalDateTime lastBorrowDate) {
        this.lastBorrowDate = lastBorrowDate;
    }

    public LocalDateTime getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDateTime expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
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

    // Business methods
    public boolean isAvailable() {
        return status == CopyStatus.AVAILABLE;
    }

    public boolean isBorrowed() {
        return status == CopyStatus.BORROWED;
    }

    public void markAsBorrowed() {
        this.status = CopyStatus.BORROWED;
        this.lastBorrowDate = LocalDateTime.now();
    }

    public void markAsReturned() {
        this.status = CopyStatus.AVAILABLE;
        this.expectedReturnDate = null;
    }

    public void markAsDamaged() {
        this.status = CopyStatus.DAMAGED;
    }

    public void markAsLost() {
        this.status = CopyStatus.LOST;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "BookRecord{" +
                "id=" + id +
                ", barcode='" + barcode + '\'' +
                ", callNumber='" + callNumber + '\'' +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", condition=" + condition +
                '}';
    }
}