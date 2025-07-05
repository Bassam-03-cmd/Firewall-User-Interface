package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserRegistration {

    private final UserRepository userRepo;

    // Utility class for input validation
    private final InputValidation inputValidation = new InputValidation();

    // Constructor-based injection of UserRepository
    public UserRegistration(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Display the Add User form (GET request)
    @GetMapping("/add-user")
    public String showAddForm(HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        // Check if a user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // Redirect to login if no user
        }

        // Check if user changed their default password
        if (!user.isPassChanged()) {
            return "redirect:/reset-password";
        }

        // Only admin can access this page
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Add empty user object to model for form binding
        model.addAttribute("userForm", new User());
        return "add-user"; // Return the add-user view
    }

    // Handle Add User form submission (POST request)
    @PostMapping("/add-user")
    public String addUser(
        @ModelAttribute("userForm") User userRequest, // Binds form inputs to user object
        HttpSession session, // Used to check logged-in user
        Model model, // Used to pass messages to view
        RedirectAttributes redirectAttrs // Flash attributes for redirects
    ) {
        // Ensure session has logged-in user
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Only admin can add users
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Check if email or username already exists
        if (userRepo.existsByUserEmail(userRequest.getUserEmail().toLowerCase())) {
            model.addAttribute("message", "Email already exists");
            return "add-user";
        }

        if (userRepo.existsByUserName(userRequest.getUserName())) {
            model.addAttribute("message", "Username already exists");
            return "add-user";
        }

        // Validate email, username, and password formats
        String validateEmail = inputValidation.IsValidEmail(userRequest.getUserEmail());
        String validateUsermae = inputValidation.IsValidUsername(userRequest.getUserName());
        String validatePassword = inputValidation.IsValidPassword(userRequest.getUserPassword());

        // Display validation errors if any
        if (!validateEmail.isEmpty()) {
            model.addAttribute("message", validateEmail);
            return "add-user";
        }

        if (!validateUsermae.isEmpty()) {
            model.addAttribute("message", validateUsermae);
            return "add-user";
        }

        if (!validatePassword.isEmpty()) {
            model.addAttribute("message", validatePassword);  
            return "add-user";
        }

        // Confirm that both password fields match
        if (!userRequest.getUserPassword().equals(userRequest.getConfirmPassword())) {
            model.addAttribute("message", "Passwords do not match");
            return "add-user";
        }

        // Create new user object and save to DB
        User newUser = new User(
            userRequest.getUserName(),
            userRequest.getUserEmail().toLowerCase(),
            userRequest.getHashPassword(userRequest.getUserPassword()),
            userRequest.getUserRole()
        );

        userRepo.save(newUser); // Persist user
        redirectAttrs.addFlashAttribute("message", "User added successfully!");
        return "redirect:/users"; // Redirect to users list page
    }
}