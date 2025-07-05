package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller 
public class DeleteUser {

    private final UserRepository userRepo;

    // Constructor injection for UserRepository
    public DeleteUser(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Handles GET request to "/delete-users" to show the user deletion page
    @GetMapping("/delete-users")
    public String showDeleteForm(HttpSession session, Model model, RedirectAttributes redirectAttrs) {

        // Get the currently logged-in user from the session
        User user = (User) session.getAttribute("loggedInUser");

        // If not logged in, redirect to login
        if (user == null) {
            return "redirect:/login";
        }

        // Only Admins can access the delete-users page
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Retrieve all users from the database
        List<User> users = userRepo.findAll();

        // Add the list of users to the model so they can be shown in the view
        model.addAttribute("users", users);

        // Return the name of the view that shows the delete users form
        return "delete-users";
    }

    // Handles POST request to delete a user
    @PostMapping("/delete-users")
    public String deleteUser(
        @RequestParam("userId") Long userId,             // ID of user to delete
        HttpSession session,                             // Session to access logged-in user
        RedirectAttributes redirectAttr                  // For passing flash messages
    ) {
        // Get the currently logged-in user from session
        User user = (User) session.getAttribute("loggedInUser");

        // Redirect to login page if user is not logged in
        if (user == null) {
            return "redirect:/login";
        }

        // Only Admin users can delete users
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttr.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Delete the user with the given ID
        userRepo.deleteById(userId);

        // If the admin deletes their own account, invalidate session and log out
        if (user.getUserId().equals(userId)) {
            session.invalidate();
            return "redirect:/login";
        }

        // Add success message and redirect back to delete-users page
        redirectAttr.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/delete-users";
    }
}
