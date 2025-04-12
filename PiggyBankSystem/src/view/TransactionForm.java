package view;

import controller.UserController;
import controller.TransactionController;
import controller.GoalController;
import model.Transaction;
import model.Goal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionForm extends JFrame {
    private int userId;
    private UserController userController;
    private TransactionController transactionController;
    private GoalController goalController;

    private JTextField amountField;
    private JComboBox<String> transactionTypeComboBox;
    private JComboBox<Goal> goalComboBox;
    private JTable transactionsTable;
    private JLabel balanceLabel;

    public TransactionForm(
        int userId,
        UserController userController, 
        TransactionController transactionController, 
        GoalController goalController
    ) {
        this.userId = userId;
        this.userController = userController;
        this.transactionController = transactionController;
        this.goalController = goalController;

        setTitle("Transactions");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top Panel - Balance
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        balanceLabel = new JLabel("Current Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(balanceLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Input and Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Transaction Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Transaction"));
        
        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);
        
        inputPanel.add(new JLabel("Transaction Type:"));
        transactionTypeComboBox = new JComboBox<>(new String[]{"Deposit", "Withdrawal"});
        inputPanel.add(transactionTypeComboBox);
        
        inputPanel.add(new JLabel("Allocate to Goal:"));
        goalComboBox = new JComboBox<>();
        loadGoalsToComboBox();
        inputPanel.add(goalComboBox);
        
        JButton submitButton = new JButton("Submit Transaction");
        submitButton.addActionListener(e -> processTransaction());
        inputPanel.add(submitButton);
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> openDashboard());
        inputPanel.add(backButton);
        
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Transactions Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        transactionsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        refreshTransactionData();
    }

    private void loadGoalsToComboBox() {
        List<Goal> goals = goalController.getUserGoals(userId);
        goalComboBox.removeAllItems();
        goalComboBox.addItem(new Goal(0, "-- No Goal --", BigDecimal.ZERO));
        for (Goal goal : goals) {
            goalComboBox.addItem(goal);
        }
    }

    private void processTransaction() {
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
            
            boolean isDeposit = "Deposit".equals(transactionTypeComboBox.getSelectedItem());
            boolean transactionSuccess;
            
            if (isDeposit) {
                transactionSuccess = transactionController.deposit(userId, amount);
            } else {
                transactionSuccess = transactionController.withdraw(userId, amount);
            }

            Goal selectedGoal = (Goal) goalComboBox.getSelectedItem();
            if (transactionSuccess && selectedGoal != null && selectedGoal.getGoalId() != 0 && isDeposit) {
                goalController.addFundsToGoal(selectedGoal.getGoalId(), amount);
            }

            if (transactionSuccess) {
                JOptionPane.showMessageDialog(this, "Transaction Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                amountField.setText("");
                refreshTransactionData();
            } else {
                JOptionPane.showMessageDialog(this, "Transaction Failed. Insufficient funds.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTransactionData() {
        BigDecimal balance = transactionController.getCurrentBalance(userId);
        balanceLabel.setText(String.format("Current Balance: $%.2f", balance));

        List<Transaction> transactions = transactionController.getTransactionHistory(userId);
        String[] columns = {"ID", "Amount", "Type", "Date"};
        Object[][] data = new Object[transactions.size()][4];
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            data[i] = new Object[]{
                t.getTransactionId(),
                String.format("$%.2f", t.getAmount()),
                t.getTransactionType(),
                t.getDate()
            };
        }
        
        transactionsTable.setModel(new DefaultTableModel(data, columns));
        loadGoalsToComboBox();
    }

    private void openDashboard() {
        new DashboardForm(userId, userController, transactionController, goalController).setVisible(true);
        dispose();
    }
}