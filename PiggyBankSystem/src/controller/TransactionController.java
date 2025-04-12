package controller;

import model.Transaction;
import model.Transaction.TransactionType;
import dao.TransactionDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionController {
    private TransactionDAO transactionDAO;

    public TransactionController() {
        this.transactionDAO = new TransactionDAO();
    }

    // Deposit money
    public boolean deposit(int userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        Transaction depositTransaction = new Transaction(
            userId, 
            amount, 
            TransactionType.DEPOSIT, 
            LocalDate.now()
        );

        return transactionDAO.addTransaction(depositTransaction);
    }

    // Withdraw money
    public boolean withdraw(int userId, BigDecimal amount) {
        // Check if sufficient balance exists
        BigDecimal currentBalance = transactionDAO.calculateUserBalance(userId);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(currentBalance) > 0) {
            return false;
        }

        Transaction withdrawalTransaction = new Transaction(
            userId, 
            amount, 
            TransactionType.WITHDRAWAL, 
            LocalDate.now()
        );

        return transactionDAO.addTransaction(withdrawalTransaction);
    }

    // Get user's transaction history
    public List<Transaction> getTransactionHistory(int userId) {
        return transactionDAO.getTransactionsByUserId(userId);
    }

    // Get user's current balance
    public BigDecimal getCurrentBalance(int userId) {
        return transactionDAO.calculateUserBalance(userId);
    }

    // Get transactions within a specific date range
    public List<Transaction> getTransactionsByDateRange(
        int userId, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        return transactionDAO.getTransactionsByDateRange(userId, startDate, endDate);
    }

    // Update an existing transaction
    public boolean updateTransaction(
        int transactionId, 
        BigDecimal amount, 
        TransactionType type, 
        LocalDate date
    ) {
        Transaction existingTransaction = transactionDAO.getTransactionsByUserId(
            // This is a simplification. In a real app, you'd fetch by transaction ID
            transactionDAO.getTransactionsByUserId(transactionId).get(0).getUserId()
        ).stream()
        .filter(t -> t.getTransactionId() == transactionId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        existingTransaction.setAmount(amount);
        existingTransaction.setTransactionType(type);
        existingTransaction.setDate(date);

        return transactionDAO.updateTransaction(existingTransaction);
    }

    // Delete a transaction
    public boolean deleteTransaction(int transactionId) {
        return transactionDAO.deleteTransaction(transactionId);
    }

    // Close database connection
    public void closeConnection() {
        transactionDAO.closeConnection();
    }
}