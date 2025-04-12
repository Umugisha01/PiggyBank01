package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private int userId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDate date;

    // Enum for transaction types
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL
    }

    // Constructor
    public Transaction(int userId, BigDecimal amount, TransactionType transactionType, LocalDate date) {
        this.userId = userId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.date = date;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Validation methods
    public boolean isValidAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isValidDate() {
        return date != null && !date.isAfter(LocalDate.now());
    }
}