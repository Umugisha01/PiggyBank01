package controller;

import model.User;
import dao.UserDAO;
import java.util.List;

public class UserController {
    private UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    // Register a new user
    public boolean registerUser(String username, String password, String name, int age) {
        // Create user object
        User newUser = new User(age, username, password, name, age);
        
        // Validate user details before registration
        if (!newUser.isValidUsername() || 
            !newUser.isValidPassword() || 
            !newUser.isValidAge()) {
            return false;
        }

        // Attempt to register user
        return userDAO.registerUser(newUser);
    }

    // Authenticate user login
    public boolean login(String username, String password) {
        return userDAO.validateLogin(username, password);
    }

    // Get user by username
    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    // Update user details
    public boolean updateUser(int userId, String name, int age) {
        User existingUser = userDAO.getUserByUsername(
            userDAO.getAllUsers().stream()
                .filter(u -> u.getUserId() == userId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getUsername()
        );

        if (existingUser == null) {
            return false;
        }

        existingUser.setName(name);
        existingUser.setAge(age);

        return userDAO.updateUser(existingUser);
    }

    // Delete user
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    // Get all users (for admin purposes)
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // Close database connection
    public void closeConnection() {
        userDAO.closeConnection();
    }
}