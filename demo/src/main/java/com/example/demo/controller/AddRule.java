package com.example.demo.controller;

import com.example.demo.models.Rule;
import com.example.demo.dto.RulesHandler;
import com.example.demo.models.User;
import com.example.demo.repo.RuleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AddRule {

    private final RuleRepository ruleRepo;
    private final InputRuleValidation inputRuleValidation = new InputRuleValidation();

    public AddRule(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    @GetMapping("/add-rule")
    public String showAddForm(HttpSession session, Model model, RedirectAttributes redirectAttrs ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        model.addAttribute("ruleForm", new Rule());
        return "add-rule";
    }

    @PostMapping("/add-rule")
    public String addRule(
        @ModelAttribute("ruleForm") RulesHandler userRequest,
        Model model,
        HttpSession session,
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

        String validatePriority = inputRuleValidation.IsValidPriority(userRequest.getRulePriority());
        if (!validatePriority.isEmpty()) {
            userRequest.setRulePriority(null);
            model.addAttribute("message", validatePriority);
            return "add-rule";
        }

        String validateProtocol = inputRuleValidation.IsValidProtocol(userRequest.getProtocol());
        if (!validateProtocol.isEmpty()) {
            userRequest.setProtocol(null);
            model.addAttribute("message", validateProtocol);
            return "add-rule";
        }

        String validateSourceIP = userRequest.getSourceIP().contains(":") ?
                inputRuleValidation.validateSingleIPv6(userRequest.getSourceIP()) :
                inputRuleValidation.validateSingleIP(userRequest.getSourceIP());
        if (!validateSourceIP.isEmpty()) {
            userRequest.setSourceIP(null);
            model.addAttribute("message", validateSourceIP);
            return "add-rule";
        }

        System.out.println("Source Port: " + userRequest.getSourcePort());
        System.out.println("destination Port: " + userRequest.getDestinationPort());

        String validateSourcePort = inputRuleValidation.ValidateSinglePORT(userRequest.getSourcePort());
        if (!validateSourcePort.isEmpty()) {
            userRequest.setSourcePort(null);
            model.addAttribute("message", validateSourcePort);
            return "add-rule";
        }
        
        String validateDestinationIP = userRequest.getDestinationIP().contains(":") ?
                inputRuleValidation.validateSingleIPv6(userRequest.getDestinationIP()) :
                inputRuleValidation.validateSingleIP(userRequest.getDestinationIP());
        if (!validateDestinationIP.isEmpty()) {
            userRequest.setDestinationIP(null);
            model.addAttribute("message", validateDestinationIP);
            return "add-rule";
        }

        String validateDestinationPort = inputRuleValidation.ValidateSinglePORT(userRequest.getDestinationPort());
        if (!validateDestinationPort.isEmpty()) {
            userRequest.setDestinationPort(null);
            model.addAttribute("message", validateDestinationPort);
            return "add-rule";
        }

        Rule newRule = new Rule();
        newRule.setProtocol(userRequest.getProtocol().replaceAll("\\s+", "").toUpperCase());
        newRule.setSourceIP(userRequest.getSourceIP().replaceAll("\\s+", "").toUpperCase());
        newRule.setSourcePort(userRequest.getSourcePort().replaceAll("\\s+", ""));
        newRule.setDestinationIP(userRequest.getDestinationIP().replaceAll("\\s+", "").toUpperCase());
        newRule.setDestinationPort(userRequest.getDestinationPort().replaceAll("\\s+", ""));
        newRule.setRulePriority(userRequest.getRulePriority());
        newRule.setRuleAction(userRequest.getRuleAction());
        newRule.setEnabled(userRequest.getEnabled());
        ruleRepo.save(newRule);
        redirectAttrs.addFlashAttribute("message", "Rule added successfully!");
        return "redirect:/rules";
    }
}