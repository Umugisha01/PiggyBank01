package view;

import controller.UserController;
import controller.TransactionController;
import controller.GoalController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationForm extends JFrame {
    private UserController userController;
    private TransactionController transactionController;
    private GoalController goalController;

    private JTextField usernameField, nameField;
    private JPasswordField passwordField;
    private JSpinner ageSpinner;
    private JButton registerButton, backToLoginButton;

    public RegistrationForm(
        UserController userController, 
        TransactionController transactionController, 
        GoalController goalController
    ) {
        this.userController = userController;
        this.transactionController = transactionController;
        this.goalController = goalController;

        setTitle("Piggy Bank Registration");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageSpinner = new JSpinner(new SpinnerNumberModel(10, 6, 120, 1));
        formPanel.add(ageSpinner, gbc);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        registerButton = new JButton("Register");
        backToLoginButton = new JButton("Back to Login");
        
        registerButton.addActionListener(e -> performRegistration());
        backToLoginButton.addActionListener(e -> openLoginForm());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void performRegistration() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        int age = (int) ageSpinner.getValue();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (username.length() < 4) {
            showError("Username must be at least 4 characters");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (age < 6) {
            showError("You must be at least 6 years old");
            return;
        }

        try {
            if (userController.registerUser(username, password, name, age)) {
                JOptionPane.showMessageDialog(this, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                openLoginForm();
            } else {
                showError("Username already exists");
            }
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void openLoginForm() {
        new LoginForm(userController, transactionController, goalController).setVisible(true);
        dispose();
    }
}