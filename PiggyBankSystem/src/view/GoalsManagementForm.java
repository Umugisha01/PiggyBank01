package view;

import controller.UserController;
import controller.TransactionController;
import controller.GoalController;
import model.Goal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class GoalsManagementForm extends JFrame {
    private int userId;
    private UserController userController;
    private TransactionController transactionController;
    private GoalController goalController;

    private JTextField goalNameField;
    private JTextField targetAmountField;
    private JTable goalsTable;

    public GoalsManagementForm(
        int userId,
        UserController userController, 
        TransactionController transactionController, 
        GoalController goalController
    ) {
        this.userId = userId;
        this.userController = userController;
        this.transactionController = transactionController;
        this.goalController = goalController;

        // Frame setup
        setTitle("Goals Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new BorderLayout(10, 10));

        // Goal Creation Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Create New Goal"));

        // Goal Name
        inputPanel.add(new JLabel("Goal Name:"));
        goalNameField = new JTextField();
        inputPanel.add(goalNameField);

        // Target Amount
        inputPanel.add(new JLabel("Target Amount:"));
        targetAmountField = new JTextField();
        inputPanel.add(targetAmountField);

        // Create Goal Button
        JButton createGoalButton = new JButton("Create Goal");
        createGoalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createGoal();
            }
        });
        inputPanel.add(createGoalButton);

        // Back to Dashboard Button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDashboard();
            }
        });
        inputPanel.add(backButton);

        add(inputPanel, BorderLayout.NORTH);

        // Goals Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Your Savings Goals"));
        goalsTable = new JTable();
        
        // Add popup menu for goal actions
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Goal");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedGoal();
            }
        });
        popupMenu.add(deleteItem);
        goalsTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(goalsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Refresh goals data
        refreshGoalsData();
    }

    private void createGoal() {
        try {
            // Validate inputs
            String goalName = goalNameField.getText().trim();
            BigDecimal targetAmount = new BigDecimal(targetAmountField.getText());

            if (goalName.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a goal name", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create goal
            boolean success = goalController.createGoal(userId, goalName, targetAmount);

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Goal Created Successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear input fields
                goalNameField.setText("");
                targetAmountField.setText("");

                // Refresh goals table
                refreshGoalsData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create goal. Please check your input.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid target amount.", 
                "Invalid Input", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshGoalsData() {
        List<Goal> goals = goalController.getUserGoals(userId);
        String[] columns = {"Goal", "Target Amount", "Current Amount", "Progress"};
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

    private void deleteSelectedGoal() {
        int selectedRow = goalsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a goal to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get goal details from the selected row
        String goalName = (String) goalsTable.getValueAt(selectedRow, 0);
        
        // Find the corresponding goal
        List<Goal> goals = goalController.getUserGoals(userId);
        Goal goalToDelete = goals.stream()
            .filter(g -> g.getGoalName().equals(goalName))
            .findFirst()
            .orElse(null);

        if (goalToDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the goal: " + goalName + "?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = goalController.deleteGoal(goalToDelete.getGoalId());
                
                if (deleted) {
                    JOptionPane.showMessageDialog(this, 
                        "Goal deleted successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshGoalsData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete goal.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void openDashboard() {
        DashboardForm dashboardForm = new DashboardForm(
            userId, 
            userController, 
            transactionController, 
            goalController
        );
        dashboardForm.setVisible(true);
        dispose();
    }
}