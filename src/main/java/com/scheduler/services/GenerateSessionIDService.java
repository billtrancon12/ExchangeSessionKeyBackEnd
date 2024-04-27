package com.scheduler.services;

public class GenerateSessionIDService {
	final private static int SESSION_ID_LENGTH = 512;
	final private static String CHARARRAY_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz*&-%/!?*+=()";
	
	public static String generateSessionID() {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < SESSION_ID_LENGTH; i++) {
			int randomCharIndex = (int)Math.floor(Math.random() * CHARARRAY_STRING.length());
			stringBuilder.append(CHARARRAY_STRING.charAt(randomCharIndex));
		}
		return stringBuilder.toString();
	}
}
