package com.scheduler.exception;

public class BadSignatureException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadSignatureException() {
		super("Signature does not match with the hash value!");
	}
}
