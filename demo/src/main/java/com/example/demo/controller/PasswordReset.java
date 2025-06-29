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

    private final UserRepository userRepo;
    private final InputValidation inputValidation;

    public PasswordReset(UserRepository userRepo) {
        this.userRepo = userRepo;
        this.inputValidation = new InputValidation();
    }

    @GetMapping("/reset-password")
    public String showResetForm(Model model) {
        model.addAttribute("req", new User());
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @ModelAttribute("req") User req,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttrs
    ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        String newPassword = req.getUserPassword();
        String confirmPassword = req.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match");
            return "reset-password";
        }

        String passwordValidation = inputValidation.IsValidPassword(newPassword);
        if (!passwordValidation.isEmpty()) {
            model.addAttribute("message", passwordValidation);
            return "reset-password";
        }
        
        user.setUserPassword(req.getHashPassword(newPassword));
        user.setPassChanged(true);
        userRepo.save(user);
        redirectAttrs.addFlashAttribute("message", "Password reseted successfully!");
        return "redirect:/dashboard";
    }
}
