package com.example.demo.controller;

import com.example.demo.models.Logs;
import com.example.demo.models.User;
import com.example.demo.repo.LogRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class Dashboard {

    private final LogRepository logRepo;

    // Constructor injection of LogRepository
    public Dashboard(LogRepository logRepo) {
        this.logRepo = logRepo;
    }

    // Root route: redirects users based on login and password reset status
    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        // If user is not logged in, redirect to login page
        if (user == null) {
            return "redirect:/login";
        }

        // If user has not changed their password, force password reset
        if (!user.isPassChanged()) {
            return "redirect:/reset-password";
        }

        // Redirect to dashboard page if authenticated and password is changed
        return "redirect:/dashboard"; 
    }

    // Dashboard route - displays packet statistics
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        // Redirect if user not logged in
        if (user == null){
            return "redirect:/login";
        } 

        // Redirect if user hasn't changed password
        if (!user.isPassChanged()) return "redirect:/reset-password";

        // Fetch all logs
        List<Logs> logs = logRepo.findAll();

        // Total number of packets logged
        int totalPackets = logs.size();
        model.addAttribute("total_packets", Collections.singletonMap("n", totalPackets));

        // Count of blocked packets (rule action = DENY)
        long blockedCount = logs.stream()
            .filter(e -> e.getRule() != null && e.getRule().getRuleAction().equals("DENY"))
            .count();
        model.addAttribute("blocked_packets", Collections.singletonMap("n", blockedCount));

        // Number of unique source IP addresses
        long uniqueSrc = logs.stream()
            .map(Logs::getSrcIp)
            .distinct()
            .count();
        model.addAttribute("unique_source_ip", Collections.singletonMap("n", uniqueSrc));

        // Count denied packets grouped by destination port
        Map<Integer, Long> countByPort = logs.stream()
            .filter(e -> e.getRule() != null && e.getRule().getRuleAction().equals("DENY"))
            .collect(Collectors.groupingBy(
                Logs::getDstPort,
                Collectors.counting()
            ));

        // Find the destination port with the highest block count
        Map.Entry<Integer, Long> top = countByPort.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .orElse(null);

        // Prepare data for "most blocked port"
        Map<String, Object> mostBlockedPort = new HashMap<>();
        if (top != null) {
            mostBlockedPort.put("destination_port", top.getKey());
            mostBlockedPort.put("name",             top.getValue());
        } else {
            mostBlockedPort.put("destination_port", "");
            mostBlockedPort.put("name",             "");
        }
        model.addAttribute("most_blocked_port", mostBlockedPort);

        return "dashboard"; // returns dashboard.html
    }

    // API route: returns dashboard stats as JSON
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public Map<String, Object> getDashboardStats() {
        List<Logs> logs = logRepo.findAll(); // fetch all logs

        Map<String, Object> result = new HashMap<>();

        // Group traffic by hour
        Map<String, Long> trafficByHour = logs.stream()
            .collect(Collectors.groupingBy(
                l -> String.format("%02d:00", l.getTimestamp().getHour()),
                TreeMap::new, // maintains order
                Collectors.counting()
            ));
        result.put("trafficVolume", trafficByHour);

        // Get top 5 destination ports with most blocked traffic
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

        // Protocol distribution (TCP, UDP, etc...)
        Map<String, Long> protocolDist = logs.stream()
            .collect(Collectors.groupingBy(Logs::getProtocol, Collectors.counting()));
        result.put("protocolUsage", protocolDist);

        // Top 5 source IPs responsible for most denied traffic
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

        // Traffic split between ALLOW and DENY rules
        Map<String, Long> allowedDenied = logs.stream()
            .filter(l -> l.getRule() != null)
            .collect(Collectors.groupingBy(
                l -> l.getRule().getRuleAction(),
                Collectors.counting()
            ));
        result.put("trafficSplit", allowedDenied);

        return result; // returns complete dashboard data as JSON
    }
}