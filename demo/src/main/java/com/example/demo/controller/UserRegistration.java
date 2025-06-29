package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserRegistration {

    private final UserRepository userRepo;
    private final InputValidation inputValidation = new InputValidation();

    public UserRegistration(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/add-user")
    public String showAddForm(HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (!user.isPassChanged()) {
            return "redirect:/reset-password";
        }

        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        model.addAttribute("userForm", new User());
        return "add-user";
    }

    @PostMapping("/add-user")
    public String addUser(
        @ModelAttribute("userForm") User userRequest,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttrs
    ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        if (userRepo.existsByUserEmail(userRequest.getUserEmail().toLowerCase())) {
            model.addAttribute("message", "Email already exists");
            return "add-user";
        }
        if (userRepo.existsByUserName(userRequest.getUserName())) {
            model.addAttribute("message", "Username already exists");
            return "add-user";
        }

        
        String validateEmail = inputValidation.IsValidEmail(userRequest.getUserEmail());
        String validateUsermae  = inputValidation.IsValidUsername(userRequest.getUserName());
        String validatePassword  = inputValidation.IsValidPassword(userRequest.getUserPassword());
       
        if (!validateEmail.isEmpty()) {
            model.addAttribute("message", validateEmail);
            return "add-user";
        }

        if (!validateUsermae.isEmpty()) {
            model.addAttribute("message", validateUsermae);
            return "add-user";
        }

        if (!validatePassword.isEmpty()) {
            model.addAttribute("message", validatePassword);  
            return "add-user";
        }

        if (!userRequest.getUserPassword().equals(userRequest.getConfirmPassword())) {
            model.addAttribute("message", "Passwords do not match");
            return "add-user";
        }

        User newUser = new User(
            userRequest.getUserName(),
            //convert email to lowercase to ensure uniqueness
            userRequest.getUserEmail().toLowerCase(),
            userRequest.getHashPassword(userRequest.getUserPassword()),
            userRequest.getUserRole()
        );
        userRepo.save(newUser);
        redirectAttrs.addFlashAttribute("message","User added successfully!");
        return "redirect:/users";
    }
}
