package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BrowseUsers {

    // Injecting the UserRepository to access users from the database
    private final UserRepository userRepo;

    public BrowseUsers(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // This method handles GET requests to "/users" and displays all users
    @GetMapping("/users")
    public String listAllUsers(
        HttpSession session,               // Used to check if a user is logged in and get their session info
        Model model,                       // Used to pass data (users list) to the view
        RedirectAttributes redirectAttrs   // Used to send flash messages between redirects
    ) {
        // Get the logged-in user from the session
        User user = (User) session.getAttribute("loggedInUser");
        
        // If no user is logged in, redirect to the login page
        if (user == null) {
            return "redirect:/login";
        }

        // If the logged-in user is not an admin, redirect to the dashboard with an error message
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // If the user is an admin, fetch all users from the database
        List<User> users = userRepo.findAll();

        // Add the users list to the model to display it in the view
        model.addAttribute("users", users);

        // Return the view name ("users.html")
        return "users";
    }
}
