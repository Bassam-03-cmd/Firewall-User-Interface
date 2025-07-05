package com.example.demo.controller;

import com.example.demo.models.Logs;
import com.example.demo.models.User;
import com.example.demo.repo.LogRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class BrowseLogs {

    // Inject LogRepository to interact with the database for log records
    private final LogRepository logRepo;

    // Constructor-based dependency injection
    public BrowseLogs(LogRepository logRepo) {
        this.logRepo = logRepo;
    }

    // Mapping for GET request to view logs
    @GetMapping("/logs")
    public String showLogs(
            @RequestParam(name = "search", required = false) String search, // Optional search parameter
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttrs
    ) {
        // Retrieve logged-in user from session
        User user = (User) session.getAttribute("loggedInUser");

        // If no user is logged in, redirect to login page
        if (user == null) {
            return "redirect:/login";
        }

        // If user has not changed the default password, force them to reset it
        if (!user.isPassChanged()) {
            redirectAttrs.addFlashAttribute("message", "Chaneging password required before first login!");
            return "redirect:/reset-password";
        }

        List<Logs> logs;

        // If search keyword is provided, perform filtered search
        if (search != null && !search.isBlank()) {
            logs = logRepo.search(search);
        } 
        // Otherwise, retrieve all logs from the repository
        else {
            logs = logRepo.findAll();
        }

        // Pass retrieved logs and the current search keyword back to the view
        model.addAttribute("logs", logs);
        model.addAttribute("search", search);

        // Render the "logs.html" page
        return "logs";
    }
}