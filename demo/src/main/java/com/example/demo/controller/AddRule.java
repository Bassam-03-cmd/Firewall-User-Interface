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

// Controller for handling the creation of firewall rules.
@Controller
public class AddRule {

    // Inject Rule repository to save rules to the database
    private final RuleRepository ruleRepo;

    // Utility class to perform rule input validation
    private final InputRuleValidation inputRuleValidation = new InputRuleValidation();

    // Constructor injection of RuleRepository
    public AddRule(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    /*
     1. GET method to show the 'Add Rule' form.
     2. Redirects to login if user is not authenticated.
     3. Redirects to dashboard if the user is not an admin.
     */
    @GetMapping("/add-rule")
    public String showAddForm(HttpSession session, Model model, RedirectAttributes redirectAttrs ) {
        User user = (User) session.getAttribute("loggedInUser");

        // If user not logged in, redirect to login
        if (user == null) {
            return "redirect:/login";
        }

        // If user is not an Admin, deny access
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Add an empty Rule object to the model for form binding
        model.addAttribute("ruleForm", new Rule());
        return "add-rule";
    }

    /*
     1. POST method to handle form submission for adding a new rule.
     2. Validates all fields and saves the rule if valid.
     */
    @PostMapping("/add-rule")
    public String addRule(
        @ModelAttribute("ruleForm") RulesHandler userRequest, // Holds form inputs
        Model model,
        HttpSession session,
        RedirectAttributes redirectAttrs 
    ) {
        // Ensure user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Ensure only Admins can add rules
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Validate Rule Priority
        String validatePriority = inputRuleValidation.IsValidPriority(userRequest.getRulePriority());
        if (!validatePriority.isEmpty()) {
            userRequest.setRulePriority(null);
            model.addAttribute("message", validatePriority);
            return "add-rule";
        }

        // Validate Protocol
        String validateProtocol = inputRuleValidation.IsValidProtocol(userRequest.getProtocol());
        if (!validateProtocol.isEmpty()) {
            userRequest.setProtocol(null);
            model.addAttribute("message", validateProtocol);
            return "add-rule";
        }

        // Validate Source IP (IPv4 or IPv6 will be validated)
        String validateSourceIP = userRequest.getSourceIP().contains(":") ?
                inputRuleValidation.validateSingleIPv6(userRequest.getSourceIP()) :
                inputRuleValidation.validateSingleIP(userRequest.getSourceIP());
        if (!validateSourceIP.isEmpty()) {
            userRequest.setSourceIP(null);
            model.addAttribute("message", validateSourceIP);
            return "add-rule";
        }

        // Validate Source Port
        String validateSourcePort = inputRuleValidation.ValidateSinglePORT(userRequest.getSourcePort());
        if (!validateSourcePort.isEmpty()) {
            userRequest.setSourcePort(null);
            model.addAttribute("message", validateSourcePort);
            return "add-rule";
        }

        // Validate Destination IP (IPv4 or IPv6 will be validated)
        String validateDestinationIP = userRequest.getDestinationIP().contains(":") ?
                inputRuleValidation.validateSingleIPv6(userRequest.getDestinationIP()) :
                inputRuleValidation.validateSingleIP(userRequest.getDestinationIP());
        if (!validateDestinationIP.isEmpty()) {
            userRequest.setDestinationIP(null);
            model.addAttribute("message", validateDestinationIP);
            return "add-rule";
        }

        // Validate Destination Port
        String validateDestinationPort = inputRuleValidation.ValidateSinglePORT(userRequest.getDestinationPort());
        if (!validateDestinationPort.isEmpty()) {
            userRequest.setDestinationPort(null);
            model.addAttribute("message", validateDestinationPort);
            return "add-rule";
        }

        // All validations passed then create and save new Rule object
        Rule newRule = new Rule();
        newRule.setProtocol(userRequest.getProtocol().replaceAll("\\s+", "").toUpperCase());
        newRule.setSourceIP(userRequest.getSourceIP().replaceAll("\\s+", "").toUpperCase());
        newRule.setSourcePort(userRequest.getSourcePort().replaceAll("\\s+", "").toUpperCase());
        newRule.setDestinationIP(userRequest.getDestinationIP().replaceAll("\\s+", "").toUpperCase());
        newRule.setDestinationPort(userRequest.getDestinationPort().replaceAll("\\s+", "").toUpperCase());
        newRule.setRulePriority(userRequest.getRulePriority());
        newRule.setRuleAction(userRequest.getRuleAction());
        newRule.setEnabled(userRequest.getEnabled());

        // Save rule to the database
        ruleRepo.save(newRule);

        // Add success message and redirect to rules page
        redirectAttrs.addFlashAttribute("message", "Rule added successfully!");
        return "redirect:/rules";
    }
}
