package com.example.demo.controller;

import com.example.demo.models.*;
import com.example.demo.repo.RuleRepository;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UpdateRule {

    private final RuleRepository ruleRepo; // Repository to manage firewall rules
    private final InputRuleValidation inputRuleValidation = new InputRuleValidation(); // Validator for rule fields

    // Constructor injection of RuleRepository
    public UpdateRule(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    // Show the page for modifying rules
    @GetMapping("/modify-rules")
    public String modifyRulesPage(HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        // Ensure the user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Only Admin users can access this page
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Fetch all rules sorted by priority and pass them to the view
        List<Rule> rules = ruleRepo.findAllByOrderByRulePriorityAsc();
        model.addAttribute("rules", rules);
        return "modify-rules"; // View name
    }

    // Handle rule modification submissions
    @PostMapping("/modify-rules")
    public String modifyRules(
        @RequestParam("ruleIds") List<Long> ruleIds,    // List of rule IDs to update
        HttpServletRequest request,                    // Raw request for accessing form inputs
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttrs
    ) {
        // Verify user is logged in
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Only Admins can modify rules
        if (!"Admin".equals(user.getUserRole())) {
            redirectAttrs.addFlashAttribute("message", "Admin access required!");
            return "redirect:/dashboard";
        }

        // Iterate over each rule ID submitted from the form
        for (Long id : ruleIds) {
            ruleRepo.findById(id).ifPresent(rule -> {
                // Retrieve form input values based on rule ID
                String rulePriority = request.getParameter("priority" + id);
                String protocol = request.getParameter("protocol" + id);
                String srcIp = request.getParameter("sourceIP" + id);
                String srcPort = request.getParameter("sourcePort" + id);
                String dstIp = request.getParameter("destinationIP" + id);
                String dstPort = request.getParameter("destinationPort" + id);
                String enabled = request.getParameter("enabled" + id);
                String action = request.getParameter("ruleAction" + id);

                // Validate and update priority
                if(rulePriority != null){
                    String validatePriority = inputRuleValidation.IsValidPriority(Integer.valueOf(rulePriority));
                    if (!validatePriority.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", validatePriority);
                        return;
                    }
                    rule.setRulePriority(Integer.valueOf(rulePriority));
                }

                // Validate and update protocol
                if (protocol != null) {
                    String protoError = inputRuleValidation.IsValidProtocol(protocol);
                    if (!protoError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", protoError);
                        return;
                    }
                    rule.setProtocol(protocol.toUpperCase());
                }

                // Validate and update source IP (IPv4 or IPv6)
                if (srcIp != null) {
                    String srcIpError = srcIp.contains(":") ?
                        inputRuleValidation.validateSingleIPv6(srcIp) :
                        inputRuleValidation.validateSingleIP(srcIp);
                    if (!srcIpError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", srcIpError);
                        return;
                    }
                    rule.setSourceIP(srcIp.toUpperCase());
                }

                // Validate and update source port
                if (srcPort != null) {
                    String srcPortError = inputRuleValidation.ValidateSinglePORT(srcPort);
                    if (!srcPortError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", srcPortError);
                        return;
                    }
                    rule.setSourcePort(srcPort.toLowerCase());
                }

                // Validate and update destination IP
                if(dstIp != null) {
                    String dstIpError = dstIp.contains(":") ?
                        inputRuleValidation.validateSingleIPv6(dstIp) :
                        inputRuleValidation.validateSingleIP(dstIp);
                    if (!dstIpError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", dstIpError);
                        return;
                    }
                    rule.setDestinationIP(dstIp.toUpperCase());
                }

                // Validate and update destination port
                if (dstPort != null) {
                    String dstPortError = inputRuleValidation.ValidateSinglePORT(dstPort);
                    if (!dstPortError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", dstPortError);
                        return;
                    }
                    rule.setDestinationPort(dstPort.toLowerCase());
                }

                // Update rule enabled status
                if(enabled != null) {
                    rule.setEnabled("1".equals(enabled));
                }

                // Update rule action
                if(action != null) {
                    rule.setRuleAction(action);
                }

                // Save the updated rule
                ruleRepo.save(rule);
            });
        }
        // After all updates, redirect to the same page
        return "redirect:/modify-rules";
    }
}
