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

@Controller
public class BrowseRules {

    private final RuleRepository ruleRepo;

    public BrowseRules(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    @GetMapping("/rules")
    public String showRules(HttpSession session, Model model, RedirectAttributes redirectAttrs ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        List<Rule> rules = ruleRepo.findAllByOrderByRulePriorityAsc();
        model.addAttribute("rules", rules);
        return "rules";
    }
}

