package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserLogin {
    private final UserRepository userRepo;
    private final InputValidation inputValidation = new InputValidation();

    public UserLogin(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("req", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(
        @ModelAttribute("req") User userRequest,
        HttpSession session,
        Model model
    ) {
        String identifier = userRequest.getUserEmail();
        String password   = userRequest.getUserPassword();
        boolean isEmail   = identifier.contains("@");

        if (isEmail) {
            String emailValidation = InputValidation.IsValidEmail(identifier);
            if (!emailValidation.isEmpty()) {
                model.addAttribute("message", emailValidation);
                return "login";
            }

            String passwordValidation = InputValidation.IsValidPassword(password);
            if (!passwordValidation.isEmpty()) {
                model.addAttribute("message", passwordValidation);
                return "login";
            }

            Optional<User> userOpt =
                userRepo.findByUserEmailAndUserPassword(identifier.toLowerCase(), userRequest.getHashPassword(password));
            if (userOpt.isEmpty()) {
                model.addAttribute("message", "Invalid email/username or password");
                return "login";
            }

            User user = userOpt.get();
            session.setAttribute("loggedInUser", user);
            if (!user.isPassChanged()) {
                model.addAttribute("message", "You must change your password before first login");
                return "redirect:/reset-password";
            }
            return "redirect:/";
        }

        else {
            String usernameValidation = InputValidation.IsValidUsername(identifier);
            if (!usernameValidation.isEmpty()) {
                model.addAttribute("message", usernameValidation);
                return "login";
            }
            String passwordValidation = InputValidation.IsValidPassword(password);
            if (!passwordValidation.isEmpty()) {
                model.addAttribute("message", passwordValidation);
                return "login";
            }

            Optional<User> userOpt =
                userRepo.findByUserNameAndUserPassword(identifier, userRequest.getHashPassword(password));
            if (userOpt.isEmpty()) {
                model.addAttribute("message", "Invalid email/username or password");
                return "login";
            }

            User user = userOpt.get();
            session.setAttribute("loggedInUser", user);

            if (!user.isPassChanged()) {
                return "redirect:/reset-password";
            }
            return "redirect:/";
        }
    }
}