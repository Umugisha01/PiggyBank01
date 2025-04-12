
import controller.UserController;
import controller.TransactionController;
import controller.GoalController;
import view.LoginForm;

public class PiggyBankApplication {
    public static void main(String[] args) {
        // Initialize controllers
        UserController userController = new UserController();
        TransactionController transactionController = new TransactionController();
        GoalController goalController = new GoalController();

        // Launch the login form
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginForm(userController, transactionController, goalController).setVisible(true);
        });
    }

    // Cleanup method to close database connections
    public static void shutdown(
        UserController userController, 
        TransactionController transactionController, 
        GoalController goalController
    ) {
        userController.closeConnection();
        transactionController.closeConnection();
        goalController.closeConnection();
    }
}