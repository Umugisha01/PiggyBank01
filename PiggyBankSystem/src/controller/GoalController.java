package controller;

import model.Goal;
import dao.GoalDAO;
import dao.TransactionDAO;
import java.math.BigDecimal;
import java.util.List;

public class GoalController {
    private GoalDAO goalDAO;
    private TransactionDAO transactionDAO;

    public GoalController() {
        this.goalDAO = new GoalDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // Create a new savings goal
    public boolean createGoal(int userId, String goalName, BigDecimal targetAmount) {
        Goal newGoal = new Goal(userId, goalName, targetAmount);
        
        // Validate goal details
        if (!newGoal.isValidTargetAmount()) {
            return false;
        }

        return goalDAO.createGoal(newGoal);
    }

    // Add funds to a specific goal
    public boolean addFundsToGoal(int goalId, BigDecimal amount) {
        // Verify sufficient balance in user's account
        Goal goal = goalDAO.getGoalById(goalId);
        if (goal == null) {
            return false;
        }

        // Check if adding funds would exceed target
        BigDecimal currentBalance = transactionDAO.calculateUserBalance(goal.getUserId());
        if (amount.compareTo(currentBalance) > 0 || 
            goal.getCurrentAmount().add(amount).compareTo(goal.getTargetAmount()) > 0) {
            return false;
        }

        return goalDAO.updateGoalProgress(goalId, amount);
    }

    // Get all goals for a user
    public List<Goal> getUserGoals(int userId) {
        return goalDAO.getGoalsByUserId(userId);
    }

    // Get completed goals
    public List<Goal> getCompletedGoals(int userId) {
        return goalDAO.getCompletedGoals(userId);
    }

    // Update goal details
    public boolean updateGoal(int goalId, String newGoalName, BigDecimal newTargetAmount) {
        Goal existingGoal = goalDAO.getGoalById(goalId);
        if (existingGoal == null) {
            return false;
        }

        existingGoal.setGoalName(newGoalName);
        existingGoal.setTargetAmount(newTargetAmount);

        return goalDAO.updateGoal(existingGoal);
    }

    // Delete a goal
    public boolean deleteGoal(int goalId) {
        return goalDAO.deleteGoal(goalId);
    }

    // Check goal progress
    public double getGoalProgressPercentage(int goalId) {
        Goal goal = goalDAO.getGoalById(goalId);
        if (goal == null) {
            return 0.0;
        }

        return (goal.getCurrentAmount().doubleValue() / goal.getTargetAmount().doubleValue()) * 100;
    }

    // Close database connection
    public void closeConnection() {
        goalDAO.closeConnection();
        transactionDAO.closeConnection();
    }
}