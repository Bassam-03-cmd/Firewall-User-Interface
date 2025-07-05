package com.example.demo.dto;

public class RulesHandler {
    private String sourceIP;
    private String destinationIP;
    private String sourcePort;
    private String destinationPort;
    private String protocol;
    private String ruleAction;
    private Integer rulePriority;
    private boolean enabled;

    public RulesHandler(String sourceIP, String destinationIP, String sourcePort, String destinationPort,
        String protocol, String ruleAction, Integer rulePriority, boolean enabled) {
        this.sourceIP  = sourceIP;
        this.destinationIP  = destinationIP;
        this.sourcePort     = sourcePort;
        this.destinationPort= destinationPort;
        this.protocol       = protocol;
        this.ruleAction     = ruleAction;
        this.rulePriority   = rulePriority;
        this.enabled        = enabled;
    }
    
    public RulesHandler() {}

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRuleAction() {
        return ruleAction;
    }

    public void setRuleAction(String ruleAction) {
        this.ruleAction = ruleAction;
    }

    public Integer getRulePriority() {
        return rulePriority;
    }

    public void setRulePriority(Integer rulePriority) {
        this.rulePriority = rulePriority;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}