package com.example.demo.controller;

import com.example.demo.models.Logs;
import com.example.demo.models.User;
import com.example.demo.repo.LogRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class Dashboard {

    private final LogRepository logRepo;

    public Dashboard(LogRepository logRepo) {
        this.logRepo = logRepo;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!user.isPassChanged()) {
            return "redirect:/reset-password";
        }

        return "redirect:/dashboard"; 
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // --- auth checks ---
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null){
            return "redirect:/login";
        } 

        if (!user.isPassChanged()) return "redirect:/reset-password";

        // --- fetch logs ---
        List<Logs> logs = logRepo.findAll();

        // --- KPI 1: total packets ---
        int totalPackets = logs.size();
        model.addAttribute("total_packets",
            Collections.singletonMap("n", totalPackets));

        // --- KPI 2: blocked packets ---
        long blockedCount = logs.stream()
            .filter(e -> e.getRule() != null && e.getRule().getRuleAction().equals("DENY"))
            .count();
        model.addAttribute("blocked_packets",
            Collections.singletonMap("n", blockedCount));

        // --- KPI 3: unique source IPs ---
        long uniqueSrc = logs.stream()
            .map(Logs::getSrcIp)
            .distinct()
            .count();
        model.addAttribute("unique_source_ip",
            Collections.singletonMap("n", uniqueSrc));

        // --- KPI 4: most blocked port ---
        // count by destination port
        Map<Integer, Long> countByPort = logs.stream()
            .filter(e -> e.getRule() != null && e.getRule().getRuleAction().equals("DENY"))
            .collect(Collectors.groupingBy(
                Logs::getDstPort,
                Collectors.counting()
            ));
        // pick the port with the max count
        Map.Entry<Integer, Long> top = countByPort.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        Map<String, Object> mostBlockedPort = new HashMap<>();
        if (top != null) {
            mostBlockedPort.put("destination_port", top.getKey());
            mostBlockedPort.put("name",             top.getValue());
        } else {
            mostBlockedPort.put("destination_port", "");
            mostBlockedPort.put("name",             "");
        }
        model.addAttribute("most_blocked_port", mostBlockedPort);

        // ... (your chart data attributes would follow here) ...

        return "dashboard";
    }

    //adnan code
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getDashboardStats() {
        List<Logs> logs = logRepo.findAll();

        Map<String, Object> result = new HashMap<>();

        // 1. Traffic Volume Over Time
        Map<String, Long> trafficByHour = logs.stream()
            .collect(Collectors.groupingBy(
                l -> String.format("%02d:00", l.getTimestamp().getHour()),
                TreeMap::new,
                Collectors.counting()
            ));
        result.put("trafficVolume", trafficByHour);

        // 2. Top Blocked Ports
        Map<Integer, Long> blockedPorts = logs.stream()
            .filter(l -> l.getRule() != null && "DENY".equals(l.getRule().getRuleAction()))
            .collect(Collectors.groupingBy(
                Logs::getDstPort,
                Collectors.counting()
            ));
        List<Map<String, Object>> topPorts = blockedPorts.entrySet().stream()
        .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
        .limit(5)
        .map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("port", e.getKey());
            m.put("count", e.getValue());
            return m;
        })
        .collect(Collectors.toList());
        result.put("blockedPorts", topPorts);

        // 3. Protocol Usage Distribution
        Map<String, Long> protocolDist = logs.stream()
            .collect(Collectors.groupingBy(Logs::getProtocol, Collectors.counting()));
        result.put("protocolUsage", protocolDist);

        // 4. Top Source IPs (Blocked)
        Map<String, Long> blockedSrcIp = logs.stream()
            .filter(l -> l.getRule() != null && "DENY".equals(l.getRule().getRuleAction()))
            .collect(Collectors.groupingBy(Logs::getSrcIp, Collectors.counting()));
        
        List<Map<String, Object>> topSrcIps = blockedSrcIp.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("ip", e.getKey());
                m.put("count", e.getValue());
                return m;
            })
            .collect(Collectors.toList());
        result.put("topSourceIps", topSrcIps);

        // 5. Allowed vs Denied
        Map<String, Long> allowedDenied = logs.stream()
            .filter(l -> l.getRule() != null)
            .collect(Collectors.groupingBy(
                l -> l.getRule().getRuleAction(),
                Collectors.counting()
            ));
        result.put("trafficSplit", allowedDenied);

        return result;
    }


    //

}