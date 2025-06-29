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

    private final UserRepository userRepo;

    public BrowseUsers(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/users")
    public String listAllUsers(HttpSession session, Model model, RedirectAttributes redirectAttrs ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Rule added successfully!");
            return "redirect:/dashboard";
        }

        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "users";
    }
}
