package com.example.demo.models;

import jakarta.persistence.*;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Table(name = "Users") 
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")                    
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 100)
    private String userPassword;

    @Column(name = "User_Role", nullable = false, length = 100)
    private String userRole;

    @Column(name = "user_status", nullable = false)
    private String userStatus;

    @Column (name = "isPassChanged", nullable = false)
    private boolean isPassChanged;

    private static String confirmPassword;
    private static final String FIXED_SALT = "$2a$10$e0NRyfmBEJEIsZj0WmIavn"; 

    public User() {}

    public User(String userName, String userEmail, String userPassword, String userRole) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userRole = userRole;
        this.userStatus = "Active";
        this.isPassChanged = false;
    }

    public User(String userName, String userPassword, String userEmail, String userRole, String confirmPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.confirmPassword = confirmPassword;
        this.userStatus = "Active";
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
    
    public boolean isPassChanged() {
        return isPassChanged;
    }

    public void setPassChanged(boolean passChanged) {
        isPassChanged = passChanged;
    }

    public String getConfirmPassword() { 
        return confirmPassword; 
    }

    public void setConfirmPassword(String confirmPassword) { 
        this.confirmPassword = confirmPassword; 
    }

    public String getHashPassword(String password) {
        return BCrypt.hashpw(password, FIXED_SALT);
    }

}