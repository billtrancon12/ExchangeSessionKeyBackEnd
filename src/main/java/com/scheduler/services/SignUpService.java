package com.scheduler.services;

import java.util.List;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.scheduler.User;
import com.scheduler.exception.NullUserException;
import com.scheduler.policies.PasswordPolicy;
import com.scheduler.repository.UserRepository;

@Service
public class SignUpService {
	@Autowired
	private UserRepository userRepository;
	private User newUser;

	public void setNewUser(String newUserData) {
		JSONObject jsonObject = new JSONObject(newUserData);
		this.newUser = new User(jsonObject.getString("email"), jsonObject.getString("password"));
	}
	
	protected List<User> list(){
		return userRepository.findAll();
	}
	
	public HttpStatus registerNewUser() throws Exception {
		if(newUser == null) {
			throw new NullUserException();
		}
		if(!validateEmail()) {
			return HttpStatus.UNAUTHORIZED;
		}
		if(isExistingUser()) {
			return HttpStatus.CONFLICT;
		}
		this.userRepository.save(newUser);
		return HttpStatus.OK;
	}
	
	public boolean validateEmail() throws Exception {
		EmailValidator validator = new EmailValidator(false, false, DomainValidator.getInstance());
		if(newUser == null) {
			return false;
		}
		if(newUser.getEmail().isBlank()) {
			return false;
		}
		return validator.isValid(newUser.getEmail());
	}
	
	public boolean validatePassword() throws Exception{
		if(newUser == null) {
			return false;
		}
		if(newUser.getPassword().isBlank()) {
			return false;
		}
		if(!PasswordPolicy.isValid(newUser.getPassword())) {
			return false;
		}
		return true;
	}
	
	public boolean isExistingUser() {
		User existingUser = userRepository.findByEmail(newUser.getEmail());
		if(existingUser == null) return false;
		return true;
	}
}
