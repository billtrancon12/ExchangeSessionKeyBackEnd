package com.scheduler.policies;

public class PasswordPolicy {
	final private static int MIN_UP_CHAR = 1;   // At least 1 uppercase char
	final private static int MIN_LOW_CHAR = 1;  // At least 1 lowercase char
	final private static int MIN_SPEC_CHAR = 1; // At least 1 special char
	final private static int MIN_LENGTH = 8; 	 // At least 8 characters long
	final private static int MIN_DIGIT = 1;    // At least 1 digit
	final private static char[] SPECIAL_CHAR_LIST = new char[] {'!','@','#','$','%','^','&','*','(',')','{','}','-','_','=','+','[',']','{','}','\\','|',';',':','\'','"',',','<','.','>','/','?','`','~'};
	
	public static boolean isValid(String password) {
		int lowChar = 0;
		int upChar = 0;
		int specChar = 0;
		int digitChar = 0;
		
		if(password.length() < MIN_LENGTH) {
			return false;
		}
		for(char c : password.toCharArray()) {
			if(isInSpecialList(c)) {
				specChar++;
			}
			else if(c - 'a' >= 0) {
				lowChar++;
			}
			else if(c - 'A' >= 0) {
				upChar++;
			}
			else if(c - '0' >= 0) {
				digitChar++;
			}
		}
		if(lowChar < MIN_LOW_CHAR) {
			return false;
		}
		if(upChar < MIN_UP_CHAR) {
			return false;
		}
		if(digitChar < MIN_DIGIT) {
			return false;
		}
		if(specChar < MIN_SPEC_CHAR) {
			return false;
		}
		return true;
	}
	
	private static boolean isInSpecialList(char c) {
		for(char letter : SPECIAL_CHAR_LIST) {
			if(letter == c) return true;
		}
		return false;
	}
}
