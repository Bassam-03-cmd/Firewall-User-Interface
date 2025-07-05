package com.example.demo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long ruleID;

    @Column(name = " src_ip", nullable = false)
    private String sourceIP;

    @Column(name = "dst_ip", nullable = false)
    private String destinationIP;

    @Column(name = "src_port", nullable = false)
    private Integer sourcePort;

    @Column(name = "dst_port", nullable = false)
    private Integer destinationPort;

    @Column(name = "protocol", nullable = false)
    private String protocol;

    @Column(name = "action", nullable = false)
    private String ruleAction;

    @Column(name = "priority", nullable = false)
    private Integer rulePriority;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;


    public Rule(String sourceIP, String destinationIP, Integer sourcePort, Integer destinationPort,
                String protocol, String ruleAction, Integer rulePriority, boolean enabled) 
    {
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.protocol = protocol;
        this.ruleAction = ruleAction;
        this.rulePriority = rulePriority;
        this.enabled = enabled;
    }

    public Rule() {}


    public Long getRuleID() {
        return ruleID;
    }

    public void setID(Long ruleID) {
        this.ruleID = ruleID;
    }

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

    public Integer getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        if(sourcePort.equalsIgnoreCase("any")){
            this.sourcePort = -1;
            return;
        }
        this.sourcePort = Integer.parseInt(sourcePort);
    }

    public Integer getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        if(destinationPort.equalsIgnoreCase("any")){
            this.destinationPort = -1;
            return;
        }
        this.destinationPort = Integer.parseInt(destinationPort);
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

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
