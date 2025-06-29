package com.example.demo.controller;

import com.example.demo.models.Logs;
import com.example.demo.models.User;
import com.example.demo.repo.LogRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BrowseLogs {

    private final LogRepository logRepo;

    public BrowseLogs(LogRepository logRepo) {
        this.logRepo = logRepo;
    }

    @GetMapping("/logs")
    public String showLogs(
            @RequestParam(name = "search", required = false) String search,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttrs
    ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isPassChanged()) {
            redirectAttrs.addFlashAttribute("message", "Chaneging password required before first login!");
            return "redirect:/reset-password";
        }

        List<Logs> logs;
        if (search != null && !search.isBlank()) {
            logs = logRepo.findAll();
        } else {
            logs = logRepo.findAll();
        }

        model.addAttribute("logs", logs);
        model.addAttribute("search", search);
        return "logs";
    }
}

