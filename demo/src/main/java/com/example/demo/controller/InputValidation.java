package com.example.demo.controller;


public class InputValidation {

	public InputValidation() {}

	/*
		Validates a username based on the following criteria:
		1. Username must not be null or empty.
		2. Username length must be between 3 and 20 characters.
		3. Username must contain only alphanumeric characters (letters and numbers).
	*/
	public static String IsValidUsername(String User_name) {
		
	    final int Min_Length = 3;
	    final int Max_Length = 35;
		if (User_name == null || User_name.trim().isEmpty()) {
			return "Invalid username: Username cannot be empty";
		}

		if (User_name.length() < Min_Length || User_name.length () > Max_Length) {
			return "Invalid username: Username must be between "+ Min_Length + " and " +Max_Length + " characters";
		}

	    if (!User_name.matches("^[a-zA-Z0-9]+$")){ 
            return "Invalid username: Only letters and numbers are allowed.";
		}

		return "";
	}

	/*
		Validates an email address based on the following criteria:
		1. Email must not be null or empty.
		2. Email must not contain any spaces.
		3. Email must match a standard pattern (example: user@example.com), allowing:
		- Alphanumeric characters, dots, underscores, and hyphens before the '@'
		- A domain name after the '@' with a 2 to 6 character top-level domain
	*/
	public static String IsValidEmail(String User_email) {

       if (User_email == null || User_email.isEmpty()) {
			return "Invalid Email: Email cannot be empty";
		}
		if (User_email.contains(" ")) {
            return "Invalid email: Email cannot contain spaces";
        }
		if (!User_email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
			return "Invalid Email";
		}

       return "";
    }

	/*
		Validates a password based on the following criteria:
		1. Password mustn't be null or empty.
		2. Password length must be between 8 and 64 characters.
		3. Password mustn't contain any spaces.
		4. Password mustn't contain at least one lowercase letter, one uppercase letter, one digit, and one special character (@#$%^&+=!).
	*/
    public static String IsValidPassword(String User_password) {
        final int Min_Length = 8;
		final int Max_Length = 64;
       
		if (User_password == null || User_password.isEmpty()) {
			return "Invalid Password: Password cannot be empty";
		}
		if (User_password.length() < Min_Length || User_password.length() > Max_Length) {
			return "Invalid Password: Must be between "+ Min_Length + " and " + Max_Length + " characters";
		}
		if (User_password.contains(" ")) {
			return "Invalid Password: Password cannot contain spaces";
			
		}
		if (!User_password.matches(".*[a-z].*")||
			!User_password.matches(".*[A-Z].*")||
			!User_password.matches(".*[0-9].*")||
			!User_password.matches(".*[@#$%^&+=!].*")) {	
			return "Invalid Password: Must contain at least one lowercase letter, one uppercase letter," + 
			        "one digit, and one special character(@#$%^&+=!)";
		}
        return "" ;
    }
}