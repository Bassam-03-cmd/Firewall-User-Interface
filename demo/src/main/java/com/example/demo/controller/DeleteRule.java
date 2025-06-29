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

@Controller
public class DeleteRule {

    private final RuleRepository ruleRepo;

    public DeleteRule(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    @GetMapping("/delete-rules")
    public String deleteRuleForm(
      HttpSession session,
      RedirectAttributes redirectAttr,
      Model model
    ) {
          User user = (User) session.getAttribute("loggedInUser");
          if (user == null) {
              redirectAttr.addFlashAttribute("message", "You must be logged in to delete rules");
              return "redirect:/login";
          }

          if (!"Admin".equals(user.getUserRole())) {
              redirectAttr.addFlashAttribute("message", "Admin access required!");
              return "redirect:/dashboard";
          }

          List<Rule> rules = ruleRepo.findAllByOrderByRulePriorityAsc();
          model.addAttribute("rules", rules);
          
          return "delete-rules"; 
        }

    @PostMapping("/deleteRule/{id}")
    public String deleteRule(
      @PathVariable Long id,
      HttpSession session,
      RedirectAttributes redirectAttr
    ) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!"Admin".equals(user.getUserRole())) {
            redirectAttr.addFlashAttribute("message", "Admin access required");
            return "redirect:/dashboard";
        }

        
        ruleRepo.deleteById(id);
        redirectAttr.addFlashAttribute("message", "Rule deleted successfully");
        return "redirect:/delete-rules";
      }
}


