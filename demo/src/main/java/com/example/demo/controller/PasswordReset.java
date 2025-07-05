package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordReset {

    private final UserRepository userRepo;        // To access and update user data
    private final InputValidation inputValidation; // Custom class to validate password strength

    // Constructor injection for dependencies
    public PasswordReset(UserRepository userRepo) {
        this.userRepo = userRepo;
        this.inputValidation = new InputValidation(); // Manually create instance of validator
    }

    // Handles GET request to show password reset form
    @GetMapping("/reset-password")
    public String showResetForm(Model model) {
        model.addAttribute("req", new User()); // Add empty User object to hold form data
        return "reset-password"; // Return view name
    }

    // Handles POST request to reset password
    @PostMapping("/reset-password")
    public String resetPassword(
            @ModelAttribute("req") User req, // User object from form submission
            HttpSession session, // Session to get the logged-in user
            Model model, // To pass messages back to the form
            RedirectAttributes redirectAttrs // To pass flash messages across redirects
    ) {
        // Get currently logged-in user
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }

        // Get new password and confirmation from the form
        String newPassword = req.getUserPassword();
        String confirmPassword = req.getConfirmPassword();

        // Check if both entered passwords match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match");
            return "reset-password"; // Reload form with error
        }

        // Validate the strength of the password using custom logic
        String passwordValidation = inputValidation.IsValidPassword(newPassword);
        if (!passwordValidation.isEmpty()) {
            model.addAttribute("message", passwordValidation);
            return "reset-password"; // Reload form with validation message
        }

        // Hash and update the user's password
        user.setUserPassword(req.getHashPassword(newPassword));
        user.setPassChanged(true); // Mark that the password has been changed
        userRepo.save(user); // Save updated user data to the database

        // Add a success message and redirect to dashboard
        redirectAttrs.addFlashAttribute("message", "Password reseted successfully!");
        return "redirect:/dashboard";
    }
}