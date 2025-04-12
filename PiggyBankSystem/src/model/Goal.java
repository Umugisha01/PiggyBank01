package model;

import java.math.BigDecimal;

public class Goal {
    private int goalId;
    private int userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;

    public Goal(int userId, String goalName, BigDecimal targetAmount) {
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = BigDecimal.ZERO;
    }

    // Getters and Setters
    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }

    @Override
    public String toString() {
        return goalName + " (Target: $" + targetAmount + ")";
    }

    public boolean isValidTargetAmount() {
        return targetAmount != null && targetAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGoalAchieved() {
        return currentAmount.compareTo(targetAmount) >= 0;
    }

    public void addFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            currentAmount = currentAmount.add(amount);
        }
    }
}