package view;

import controller.UserController;
import controller.TransactionController;
import controller.GoalController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private UserController userController;
    private TransactionController transactionController;
    private GoalController goalController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginForm(
        UserController userController, 
        TransactionController transactionController, 
        GoalController goalController
    ) {
        this.userController = userController;
        this.transactionController = transactionController;
        this.goalController = goalController;

        // Frame setup
        setTitle("Piggy Bank Login");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Piggy Bank Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        add(loginButton, gbc);

        // Register Button
        gbc.gridy = 4;
        registerButton = new JButton("Register New Account");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationForm();
            }
        });
        add(registerButton, gbc);
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userController.login(username, password)) {
            // Get user ID
            int userId = userController.getUserByUsername(username).getUserId();

            // Open Dashboard
            DashboardForm dashboardForm = new DashboardForm(
                userId, 
                userController, 
                transactionController, 
                goalController
            );
            dashboardForm.setVisible(true);

            // Close login form
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistrationForm() {
        RegistrationForm registrationForm = new RegistrationForm(
            userController, 
            transactionController, 
            goalController
        );
        registrationForm.setVisible(true);
        dispose();
    }
}