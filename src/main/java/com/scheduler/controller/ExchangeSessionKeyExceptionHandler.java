package com.scheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.scheduler.exception.BadSignatureException;

@ControllerAdvice(assignableTypes = DataController.class)
public class ExchangeSessionKeyExceptionHandler {
	public ExchangeSessionKeyExceptionHandler() {}
	
	@ExceptionHandler(BadSignatureException.class)
	public ResponseEntity<String> badSignatureError(){
		return new ResponseEntity<String>("Failed to verify signature", HttpStatus.BAD_REQUEST);
	}
}
