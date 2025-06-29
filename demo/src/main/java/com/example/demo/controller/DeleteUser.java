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
    public DeleteUser(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/delete-users")
    public String showDeleteForm(HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "delete-users";
    }

    @PostMapping("/delete-users")
    public String deleteUser(
        @RequestParam("userId") Long userId,
        HttpSession session,
        RedirectAttributes redirectAttr
    ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!"Admin".equals(user.getUserRole())) {
            redirectAttr.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        userRepo.deleteById(userId);
        if (user.getUserId().equals(userId)) {
            session.invalidate();
            return "redirect:/login";
        }
        redirectAttr.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/delete-users";
    }
}
