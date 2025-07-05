package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UpdateUser {

    private final UserRepository userRepo;

    // Constructor to inject the UserRepository
    public UpdateUser(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Handle GET request to show the user modification form
    @GetMapping("/modify-users")
    public String showModifyForm(HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        // Get the logged-in user from the session
        User user = (User) session.getAttribute("loggedInUser");

        // Redirect to login page if no user is logged in
        if (user == null) {
            return "redirect:/login";
        }

        // Only allow access if the user is an Admin
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Fetch all users and pass them to the view
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "modify-users"; // Returns the view name
    }

    // Handle POST request to update users
    @PostMapping("/modify-users")
    public String processModify(
            @RequestParam("user-ids") List<Long> userIds, // List of user IDs to be updated
            HttpServletRequest request, // Used to extract form data
            HttpSession session, // Session to get logged-in user
            RedirectAttributes redirectAttrs, // Used to add flash messages
            Model model // Not really needed in POST (message should use redirectAttrs)
    ) {
        // Get the current user from session
        User user = (User) session.getAttribute("loggedInUser");

        // Redirect if not logged in
        if (user == null) {
            return "redirect:/login";
        }

        // Redirect if not an Admin
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Loop through all selected user IDs
        for (Long id : userIds) {
            userRepo.findById(id).ifPresent(u -> {
                // Get the new role and status from the request parameters
                String newRole   = request.getParameter("role-" + id);
                String newStatus = request.getParameter("enabled-" + id);

                // Update user role if provided
                if (newRole != null) {
                    u.setUserRole(newRole);
                }

                // Update user status if provided
                if (newStatus != null) {
                    u.setUserStatus(newStatus);
                }

                // Save updated user to the database
                userRepo.save(u);

                // Update the logged-in user's role in the session if it's their own account
                User refreshed = userRepo.findById(user.getUserId()).orElse(user);
                session.setAttribute("loggedInUser", refreshed);
                user.setUserRole(refreshed.getUserRole());  // Sync the role
            });
        }

        // If the user loses Admin rights after update, redirect to dashboard
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Flash success message
        redirectAttrs.addFlashAttribute("message", "Users updated successfully!");

        // Redirect to the modify-users page again
        return "redirect:/modify-users";
    }
}