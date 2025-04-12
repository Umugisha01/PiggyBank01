package model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private int age;

    // Fixed constructor
    public User(int userId, String username, String password, String name, int age) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public boolean isValidAge() { return age > 5; }
    public boolean isValidUsername() { return username != null && !username.trim().isEmpty(); }
    public boolean isValidPassword() { return password != null && password.length() >= 6; }
}