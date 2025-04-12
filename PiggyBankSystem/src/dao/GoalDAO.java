package dao;

import model.Goal;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {
    private Connection connection;

    public GoalDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new goal
    public boolean createGoal(Goal goal) {
        // Validate goal before insertion
        if (!goal.isValidTargetAmount()) {
            return false;
        }

        String sql = "INSERT INTO Goals (user_id, goal_name, target_amount, current_amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, goal.getUserId());
            pstmt.setString(2, goal.getGoalName());
            pstmt.setBigDecimal(3, goal.getTargetAmount());
            pstmt.setBigDecimal(4, goal.getCurrentAmount());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goal.setGoalId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get goals for a specific user
    public List<Goal> getGoalsByUserId(int userId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM Goals WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Goal goal = new Goal(
                        rs.getInt("user_id"),
                        rs.getString("goal_name"),
                        rs.getBigDecimal("target_amount")
                    );
                    goal.setGoalId(rs.getInt("goal_id"));
                    goal.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    goals.add(goal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

    // Update goal progress
    public boolean updateGoalProgress(int goalId, BigDecimal additionalAmount) {
        String sql = "UPDATE Goals SET current_amount = current_amount + ? WHERE goal_id = ? AND current_amount + ? <= target_amount";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, additionalAmount);
            pstmt.setInt(2, goalId);
            pstmt.setBigDecimal(3, additionalAmount);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get a specific goal by its ID
    public Goal getGoalById(int goalId) {
        String sql = "SELECT * FROM Goals WHERE goal_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, goalId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Goal goal = new Goal(
                        rs.getInt("user_id"),
                        rs.getString("goal_name"),
                        rs.getBigDecimal("target_amount")
                    );
                    goal.setGoalId(rs.getInt("goal_id"));
                    goal.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    return goal;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update goal details
    public boolean updateGoal(Goal goal) {
        // Validate goal before update
        if (!goal.isValidTargetAmount()) {
            return false;
        }

        String sql = "UPDATE Goals SET goal_name = ?, target_amount = ? WHERE goal_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, goal.getGoalName());
            pstmt.setBigDecimal(2, goal.getTargetAmount());
            pstmt.setInt(3, goal.getGoalId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a goal
    public boolean deleteGoal(int goalId) {
        String sql = "DELETE FROM Goals WHERE goal_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, goalId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get completed goals for a user
    public List<Goal> getCompletedGoals(int userId) {
        List<Goal> completedGoals = new ArrayList<>();
        String sql = "SELECT * FROM Goals WHERE user_id = ? AND current_amount >= target_amount";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Goal goal = new Goal(
                        rs.getInt("user_id"),
                        rs.getString("goal_name"),
                        rs.getBigDecimal("target_amount")
                    );
                    goal.setGoalId(rs.getInt("goal_id"));
                    goal.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    completedGoals.add(goal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return completedGoals;
    }

    // Close the database connection
    public void closeConnection() {
        DatabaseConnection.closeConnection();
    }
}