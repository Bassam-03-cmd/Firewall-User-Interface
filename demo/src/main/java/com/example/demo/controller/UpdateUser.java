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

    public UpdateUser(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/modify-users")
    public String showModifyForm(HttpSession session, Model model, RedirectAttributes redirectAttrs) {
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
        return "modify-users";
    }

    @PostMapping("/modify-users")
    public String processModify(
            @RequestParam("user-ids") List<Long> userIds,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttrs,
            Model model
    ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        for (Long id : userIds) {
            userRepo.findById(id).ifPresent(u -> {
                String newRole   = request.getParameter("role-" + id);
                String newStatus = request.getParameter("enabled-" + id);

                if (newRole != null) {
                    u.setUserRole(newRole);
                }
                if (newStatus != null) {
                    u.setUserStatus(newStatus);
                }
                userRepo.save(u);
                model.addAttribute("message","Users updated successfully!");
                User refreshed = userRepo.findById(user.getUserId()).orElse(user);
                session.setAttribute("loggedInUser", refreshed);
                user.setUserRole(refreshed.getUserRole());
            });
        }
        
        if(!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }
        return "redirect:/modify-users";
    }
}
