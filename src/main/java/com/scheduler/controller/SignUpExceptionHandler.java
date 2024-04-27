package com.scheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.scheduler.exception.NullUserException;

@ControllerAdvice(assignableTypes = DataController.class)
public class SignUpExceptionHandler{
	public SignUpExceptionHandler() {}
		
	@ExceptionHandler(NullUserException.class)
	public ResponseEntity<String> nullUserError(){
		return new ResponseEntity<String>("The user is null", HttpStatus.BAD_REQUEST);
	}
	
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<String> otherErrors(){
//		return new ResponseEntity<String>("Something is wrong", HttpStatus.BAD_REQUEST);
//	}
}
