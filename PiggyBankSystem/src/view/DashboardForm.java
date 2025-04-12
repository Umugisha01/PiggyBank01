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
import java.util.List;

public class DashboardForm extends JFrame {
    private int userId;
    private UserController userController;
    private TransactionController transactionController;
    private GoalController goalController;
    
    private JLabel balanceLabel;
    private JTable recentTransactionsTable;
    private JTable goalsTable;

    public DashboardForm(
        int userId,
        UserController userController, 
        TransactionController transactionController, 
        GoalController goalController
    ) {
        this.userId = userId;
        this.userController = userController;
        this.transactionController = transactionController;
        this.goalController = goalController;

        setTitle("Piggy Bank Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel - Balance and Actions
        JPanel topPanel = new JPanel(new BorderLayout());
        balanceLabel = new JLabel("Current Balance: $0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(balanceLabel, BorderLayout.NORTH);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton transactionButton = new JButton("Manage Transactions");
        JButton goalsButton = new JButton("Manage Goals");
        JButton logoutButton = new JButton("Logout");
        actionPanel.add(transactionButton);
        actionPanel.add(goalsButton);
        actionPanel.add(logoutButton);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Content Panel for Scrolling
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Transactions Panel
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        recentTransactionsTable = new JTable();
        JScrollPane transactionsScrollPane = new JScrollPane(recentTransactionsTable);
        transactionsPanel.add(transactionsScrollPane, BorderLayout.CENTER);
        contentPanel.add(transactionsPanel);

        // Goals Panel
        JPanel goalsPanel = new JPanel(new BorderLayout());
        goalsPanel.setBorder(BorderFactory.createTitledBorder("Savings Goals"));
        goalsTable = new JTable();
        JScrollPane goalsScrollPane = new JScrollPane(goalsTable);
        goalsPanel.add(goalsScrollPane, BorderLayout.CENTER);
        contentPanel.add(goalsPanel);

        // Wrap Content in Scroll Pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        transactionButton.addActionListener(e -> openTransactionForm());
        goalsButton.addActionListener(e -> openGoalsForm());
        logoutButton.addActionListener(e -> logout());

        refreshDashboardData();
    }

    private void refreshDashboardData() {
        // Update Balance
        BigDecimal balance = transactionController.getCurrentBalance(userId);
        balanceLabel.setText(String.format("Current Balance: $%.2f", balance));

        // Update Transactions (show all transactions now)
        List<Transaction> transactions = transactionController.getTransactionHistory(userId);
        updateTransactionTable(transactions);

        // Update Goals
        List<Goal> goals = goalController.getUserGoals(userId);
        updateGoalTable(goals);
    }

    private void updateTransactionTable(List<Transaction> transactions) {
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
        recentTransactionsTable.setModel(new DefaultTableModel(data, columns));
    }

    private void updateGoalTable(List<Goal> goals) {
        String[] columns = {"Goal", "Target", "Current", "Progress"};
        Object[][] data = new Object[goals.size()][4];
        for (int i = 0; i < goals.size(); i++) {
            Goal g = goals.get(i);
            double progress = goalController.getGoalProgressPercentage(g.getGoalId());
            data[i] = new Object[]{
                g.getGoalName(),
                String.format("$%.2f", g.getTargetAmount()),
                String.format("$%.2f", g.getCurrentAmount()),
                String.format("%.1f%%", progress)
            };
        }
        goalsTable.setModel(new DefaultTableModel(data, columns));
    }

    private void openTransactionForm() {
        TransactionForm transactionForm = new TransactionForm(
            userId, userController, transactionController, goalController);
        transactionForm.setVisible(true);
        dispose();
    }

    private void openGoalsForm() {
        GoalsManagementForm goalsForm = new GoalsManagementForm(
            userId, userController, transactionController, goalController);
        goalsForm.setVisible(true);
        dispose();
    }

    private void logout() {
        LoginForm loginForm = new LoginForm(userController, transactionController, goalController);
        loginForm.setVisible(true);
        dispose();
    }
}