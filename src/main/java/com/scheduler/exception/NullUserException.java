package com.scheduler.exception;

public class NullUserException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullUserException() {
		super("Null user found!");
	}
}

