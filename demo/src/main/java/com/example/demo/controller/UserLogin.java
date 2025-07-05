package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserLogin {

    private final UserRepository userRepo;

    // Instance of custom input validation class
    private final InputValidation inputValidation = new InputValidation();

    // Constructor injection of UserRepository
    public UserLogin(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Show the login form
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Attach empty User object to bind form inputs
        model.addAttribute("req", new User());
        return "login"; // Return the login view
    }

    // Process the login form submission
    @PostMapping("/login")
    public String login(
        @ModelAttribute("req") User userRequest, // Automatically bind form data to a User object
        HttpSession session, // Session to store logged-in user
        Model model // Model to pass messages to the view
    ) {
        String identifier = userRequest.getUserEmail();     // Could be email or username
        String password   = userRequest.getUserPassword();  // Plain-text password
        boolean isEmail   = identifier.contains("@");       // Check if input is email

        // -------- EMAIL LOGIN --------
        if (isEmail) {
            // Validate email format
            String emailValidation = InputValidation.IsValidEmail(identifier);
            if (!emailValidation.isEmpty()) {
                model.addAttribute("message", emailValidation);
                return "login";
            }

            // Validate password format
            String passwordValidation = InputValidation.IsValidPassword(password);
            if (!passwordValidation.isEmpty()) {
                model.addAttribute("message", passwordValidation);
                return "login";
            }

            // Try to find user by email and hashed password
            Optional<User> userOpt =
                userRepo.findByUserEmailAndUserPassword(
                    identifier.toLowerCase(),
                    userRequest.getHashPassword(password)
                );

            // Check if user exists
            if (userOpt.isEmpty()) {
                model.addAttribute("message", "Invalid email/username or password");
                return "login";
            }

            // Set user in session
            User user = userOpt.get();
            session.setAttribute("loggedInUser", user);

            // Check if user is logging in for the first time
            if (!user.isPassChanged()) {
                model.addAttribute("message", "You must change your password before first login");
                return "redirect:/reset-password";
            }

            return "redirect:/"; // Redirect to home/dashboard
        }

        // -------- USERNAME LOGIN --------
        else {
            // Validate username format
            String usernameValidation = InputValidation.IsValidUsername(identifier);
            if (!usernameValidation.isEmpty()) {
                model.addAttribute("message", usernameValidation);
                return "login";
            }

            // Validate password format
            String passwordValidation = InputValidation.IsValidPassword(password);
            if (!passwordValidation.isEmpty()) {
                model.addAttribute("message", passwordValidation);
                return "login";
            }

            // Try to find user by username and hashed password
            Optional<User> userOpt =
                userRepo.findByUserNameAndUserPassword(
                    identifier,
                    userRequest.getHashPassword(password)
                );

            // Check if user exists
            if (userOpt.isEmpty()) {
                model.addAttribute("message", "Invalid email/username or password");
                return "login";
            }

            // Set user in session
            User user = userOpt.get();
            session.setAttribute("loggedInUser", user);

            // Check if password has been changed
            if (!user.isPassChanged()) {
                return "redirect:/reset-password";
            }

            return "redirect:/"; // Redirect to home
        }
    }
}