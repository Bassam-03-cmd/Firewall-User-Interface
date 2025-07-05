package com.example.demo.controller;

import com.example.demo.models.Rule;
import com.example.demo.models.User;
import com.example.demo.repo.RuleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller  // Marks this class as a Spring MVC controller
public class DeleteRule {

    private final RuleRepository ruleRepo;

    // Constructor-based dependency injection for the RuleRepository
    public DeleteRule(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    // Handles GET requests to "/delete-rules" to show the list of rules for deletion
    @GetMapping("/delete-rules")
    public String deleteRuleForm(
      HttpSession session,
      RedirectAttributes redirectAttr,
      Model model
    ) {
          // Retrieve the currently logged-in user from session
          User user = (User) session.getAttribute("loggedInUser");

          // If user is not logged in, redirect to login page with a message
          if (user == null) {
              redirectAttr.addFlashAttribute("message", "You must be logged in to delete rules");
              return "redirect:/login";
          }

          // Only admin users can access this functionality
          if (!"Admin".equals(user.getUserRole())) {
              redirectAttr.addFlashAttribute("message", "Admin access required!");
              return "redirect:/dashboard";
          }

          // Retrieve all rules sorted by priority
          List<Rule> rules = ruleRepo.findAllByOrderByRulePriorityAsc();

          // Add the list of rules to the model to display them in the view
          model.addAttribute("rules", rules);

          // Return the view name "delete-rules"
          return "delete-rules"; 
    }

    // Handles POST requests to delete a specific rule based on its ID
    @PostMapping("/deleteRule/{id}")
    public String deleteRule(
      @PathVariable Long id,                      // Path variable for rule ID
      HttpSession session,                        // Current user session
      RedirectAttributes redirectAttr             // For flash messages
    ) {

        // Retrieve logged-in user from session
        User user = (User) session.getAttribute("loggedInUser");

        // Redirect to login if user is not logged in
        if (user == null) {
            return "redirect:/login";
        }

        // Check for admin role; if not admin, redirect with message
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttr.addFlashAttribute("message", "Admin access required");
            return "redirect:/dashboard";
        }

        // Perform deletion of the rule by its ID
        ruleRepo.deleteById(id);

        // Set a success message to be displayed on redirect
        redirectAttr.addFlashAttribute("message", "Rule deleted successfully");

        // Redirect back to the rule deletion page
        return "redirect:/delete-rules";
    }
}
