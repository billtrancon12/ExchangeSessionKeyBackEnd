package com.scheduler.auth;

public interface LoginInterface{
	// Return true if login successfully
	public boolean login(String email, String password);
	
	// Return true if the email is verified
	public boolean verifyEmail(String email);
	
	// Return true if the password is verified
	public boolean verifyPassword(String password);
	
	// Return true if the email is in the correct format
	public boolean isEmail(String email);
}