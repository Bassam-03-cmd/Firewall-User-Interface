package com.example.demo.controller;

public class InputRuleValidation {
    /*
        validateSingleIP():
        1-Check for null or empty input.
        2-Allow "any" as a valid input. 
        3-Validate overall IP format using regex (X.X.X.X pattern)
        4-for each octet:
            a-Check if it contains only numeric characters.
            b-Check for leading zeros.
            c-Convert to integer and check if it is in the range of 0-255.
    */
    public String validateSingleIP(String IP) {
        IP=IP.replaceAll("\\s+", "");
        if (IP.isEmpty()) {
            return "IP address cannot be null or empty.";
        }
        if ("any".equalsIgnoreCase(IP)) {
            return "";
        }
        if (!IP.matches("^(\\d{1,3}\\.){3}\\d{1,3}$")) {
            return "IP address must be in the format X.X.X.X where X is 0-255. or 'any'.";
        }
        String[] parts = IP.split("\\.");

        for (String part : parts) {
            if (!part.matches("\\d+")) { 
                return "Each octet must contain only numeric characters.";
            }
            if (part.length() > 1 && part.startsWith("0")) {
                return "Octet cannot have leading zeros.";
            }
            int octet = Integer.parseInt(part);
            if (octet < 0 || octet > 255) {
                return "Each octet must be between 0 and 255.";
            }
        }

        return ""; 
    }

    /*
        validateSingleIPv6():
        1-Check for null or empty input.
        2-Allow "any" as a valid input.  
        3-Check for valid IPv6 format.
        4-Check for leading zeros in each hextet.
        5-Check total hextet count when expanded.
    */

    public String validateSingleIPv6(String IP) {
        IP = IP.replaceAll("\\s+", "");
        if (IP.isEmpty()) {
            return "IP address cannot be null or empty.";
        }
        if ("any".equalsIgnoreCase(IP)) {
            return "";
        }
    
        if (!IP.matches("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$") && 
            !IP.matches("^((?:[0-9a-fA-F]{1,4}(?::[0-9a-fA-F]{1,4})*)?)::((?:[0-9a-fA-F]{1,4}(?::[0-9a-fA-F]{1,4})*)?)$")) {
            return "IPv6 address must be in hexadecimal format with 8 hextets separated by colons.";
        }

        String[] parts = IP.split(":");
        int emptyCount = 0;

        for (String part : parts) {
            if (part.isEmpty()) {
                emptyCount++;
                if (emptyCount > 1) {
                    return "IPv6 address can only have one :: sequence.";
                }
                continue;
            }
            
            if (!part.matches("[0-9a-fA-F]{1,4}")) {
                return "Each hextet must contain 1-4 hexadecimal characters.";
            }
            
            if (part.length() > 1 && part.replaceFirst("^0+", "").isEmpty()) {
                return "Hextet cannot be all zeros (use :: instead for compression).";
            }
        }

        int totalHextets = parts.length;
        if (IP.contains("::")) {
            totalHextets += 8 - parts.length;
        }
        
        if (totalHextets != 8 && !IP.contains("::")) {
            return "IPv6 address must have exactly 8 hextets (use :: for compression).";
        }

        return "";
    }


    /*
        ValidateSinglePORT():
        1-Check for null or empty input.
        2-Allow "any" as a valid input.
        3-Check length constraints (1 to 5 characters).
        4-Check if it contains only numeric characters.
        5-Convert to integer and check if it is within the range of 1-65535.
        6-Check if it is not equal to 0.
    
    */

    public String ValidateSinglePORT(String PORT) {
        final int Min_Length=1;
        final int Max_Length=5;

        PORT=PORT.replaceAll("\\s+", "");
        if (PORT.isEmpty()) {
            return "Port cannot be null or empty";
        }
        if ("any".equalsIgnoreCase(PORT) || "-1".equals(PORT)) {
            return "";
        }
        if (PORT.length() < Min_Length|| PORT.length() > Max_Length) {
            return "Port must be between 1 and 5 characters long.";
        }
        if (!PORT.matches("\\d+")){ 
            return "Port must contain only numeric characters.";
        }
        int portNumber = Integer.parseInt(PORT);
        if (portNumber <= 1 || portNumber >= 65535) {
            return "Port must be from 1 to 65535.";
        }
        return "";
	}

    /*
        IsValidProtocol():
        1-Check for null or empty input. 
        2-Allow "any" as a valid input.
        3-Check if it matches the allowed protocols (tcp, udp, icmp, any).
     */

    public String IsValidProtocol(String protocol) {
        protocol = protocol.replaceAll("\\s+", "").toLowerCase();
        if (protocol.isEmpty()) {
            return "Protocol cannot be null or empty.";
        }

        if (!protocol.matches("^(tcp|udp|icmp|any)$")) {
            return "Protocol must be 'TCP', 'UDP', 'ICMP', or 'ANY'.";
        }
        if ("any".equalsIgnoreCase(protocol)) {
            return "";
        }

		return "";
	}
    
    /*
        IsValidPriority():
        1-Check for null or empty input. 
        2-Allow only priorty to be greater than zero.
    */
    public String IsValidPriority(Integer priority) {
        if (priority == null) {
            return "Priority cannot be null or empty.";
        }

        if (priority <= 0) {
            return "Priority must be greater than zero.";
        }
        return "";
    }
}