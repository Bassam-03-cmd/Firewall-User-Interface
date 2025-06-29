package com.example.demo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "src_ip")
    private String srcIp;

    @Column(name = "dst_ip")
    private String dstIp;

    @Column(name = "src_port")
    private Integer srcPort;

    @Column(name = "dst_port")
    private Integer dstPort;

    @Column(name = "src_mac")
    private String srcMAC;

    @Column(name = "dst_mac")
    private String dstMAC;

    @Lob
    @Column(name = "packet", columnDefinition = "VARBINARY(MAX)")
    private byte[] packet;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "rule_id", foreignKey = @ForeignKey(name = "FK_logs_rules"))
    private Rule rule;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

    public Integer getDstPort() {
        return dstPort;
    }

    public void setDstPort(Integer dstPort) {
        this.dstPort = dstPort;
    }

    public byte[] getPacket() {
        return packet;
    }

    public void setSrcMAC(String srcMAC){
        this.srcMAC = srcMAC; 
    }

    public String getSrcMAC(){
        return srcMAC;
    }

    public void setDstMAC(String dstMAC){
        this.dstMAC = dstMAC; 
    }

    public String getDstMAC(){
        return dstMAC;
    }

    public void setPacket(byte[] packet) {
        this.packet = packet;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
