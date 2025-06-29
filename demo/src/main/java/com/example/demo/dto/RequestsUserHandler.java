// package com.example.demo.dto;

// import org.springframework.security.crypto.bcrypt.BCrypt;

// public class RequestsUserHandler {
//     private String userName;             
//     private String userEmail;        
//     private String userPassword;          
//     private String userRole;              
//     private String userStatus;
//     private String confirmPassword;
//     private static final String FIXED_SALT = "$2a$10$e0NRyfmBEJEIsZj0WmIavn"; 

//     public RequestsUserHandler() { }

//     public RequestsUserHandler(String userName, String userPassword, String userEmail, String userRole, String confirmPassword) {
//         this.userName = userName;
//         this.userPassword = userPassword;
//         this.userEmail = userEmail;
//         this.userRole = userRole;
//         this.confirmPassword = confirmPassword;
//         this.userStatus = "Active";

//     }

//     public RequestsUserHandler(String userEmail, String userPassword) {
//         this.userEmail = userEmail;
//         this.userPassword = userPassword;
//     }

//     public String getUserName() {
//         return userName;
//     }

//     public void setUserName(String userName) {
//         this.userName = userName;
//     }

//     public String getUserPassword() {
//         return userPassword;
//     }

//     public void setUserPassword(String userPassword) {
//         this.userPassword = userPassword;
//     }

//     public String getUserEmail() {
//         return userEmail;
//     }

//     public void setUserEmail(String userEmail) {
//         this.userEmail = userEmail;
//     }

//     public String getUserRole() {
//         return userRole; // Default role, can be changed based on requirements
//     }

//     public void setUserRole(String userRole) {
//         this.userRole = userRole;
//     }

//     public String getUserStatus() {
//         return userStatus;
//     }

//     public void setUserStatus(String userStatus) {
//         this.userStatus = userStatus;
//     }

//     public String getConfirmPassword() {
//         return confirmPassword;
//     }

//     public void setConfirmPassword(String confirmPassword) {
//         this.confirmPassword = confirmPassword;
//     }

//     // Password hashing with sha256 without any salt
//     public String getHashPassword(String password) {
//         return BCrypt.hashpw(password, FIXED_SALT);
//     }
   
// }