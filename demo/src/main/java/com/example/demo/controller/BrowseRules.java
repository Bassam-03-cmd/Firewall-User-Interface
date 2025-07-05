package com.example.demo.controller;

import com.example.demo.models.Rule;
import com.example.demo.models.User;
import com.example.demo.repo.RuleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller // Marks this class as a Spring MVC Controller
public class BrowseRules {

    private final RuleRepository ruleRepo;

    // Constructor-based dependency injection for RuleRepository
    public BrowseRules(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    // Handles GET requests to "/rules" — the page that displays all rules
    @GetMapping("/rules")
    public String showRules(HttpSession session, Model model, RedirectAttributes redirectAttrs) {

        // Check if the user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; // Redirect to login if not authenticated
        }

        // Check if the logged-in user has the "Admin" role
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard"; // Redirect if not an admin
        }

        // Get all rules from the database, sorted by priority (ascending)
        List<Rule> rules = ruleRepo.findAllByOrderByRulePriorityAsc();

        // Add the rule list to the model so the view (HTML) can display them
        model.addAttribute("rules", rules);

        // Return the view name "rules" (maps to rules.html or rules.jsp)
        return "rules";
    }
}
