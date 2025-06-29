package com.example.demo.controller;

import com.example.demo.models.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.*;
import com.example.demo.repo.RuleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class UpdateRule {

    private final RuleRepository ruleRepo;
    private final InputRuleValidation inputRuleValidation = new InputRuleValidation();

    public UpdateRule(RuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }

    @GetMapping("/modify-rules")
    public String modifyRulesPage(HttpSession session, Model model, RedirectAttributes redirectAttrs) {
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
        return "modify-rules";
    }

    @PostMapping("/modify-rules")
    public String modifyRules(
        @RequestParam("ruleIds") List<Long> ruleIds,
        HttpServletRequest request,
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

        for (Long id : ruleIds) {
            ruleRepo.findById(id).ifPresent(rule -> {
                String rulePriority = request.getParameter("priority" + id);
                String protocol = request.getParameter("protocol" + id);
                String srcIp = request.getParameter("sourceIP" + id);
                String srcPort = request.getParameter("sourcePort" + id);
                String dstIp = request.getParameter("destinationIP" + id);
                String dstPort = request.getParameter("destinationPort" + id);
                String enabled = request.getParameter("enabled" + id);
                String action = request.getParameter("ruleAction" + id);

                if(rulePriority != null){
                    String validatePriority = inputRuleValidation.IsValidPriority(Integer.valueOf(rulePriority));
                    if (!validatePriority.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", validatePriority);
                        return;
                    }
                    rule.setRulePriority(Integer.valueOf(rulePriority));
                }
                
                if (protocol != null) {
                    String protoError = inputRuleValidation.IsValidProtocol(protocol);
                    if (!protoError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", protoError);
                        return;
                    }
                    rule.setProtocol(protocol.toUpperCase());
                }

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

                if (srcPort != null) {
                    System.out.println(srcPort);
                    String srcPortError = inputRuleValidation.ValidateSinglePORT(srcPort);
                    if (!srcPortError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", srcPortError);
                        return;
                    }
                    rule.setSourcePort(srcPort.toLowerCase());
                }

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

                if (dstPort != null) {
                    System.out.println(dstPort);
                    String dstPortError = inputRuleValidation.ValidateSinglePORT(dstPort);
                    if (!dstPortError.isEmpty()) {
                        redirectAttrs.addFlashAttribute("message", dstPortError);
                        return;
                    }
                    rule.setDestinationPort(dstPort.toLowerCase());
                }

                if(enabled!= null) {
                    rule.setEnabled("1".equals(enabled));
                }

                if(action!= null) {
                    rule.setRuleAction(action);
                }
                
                ruleRepo.save(rule);
            });
        }
        
        return "redirect:/modify-rules";
    }
}