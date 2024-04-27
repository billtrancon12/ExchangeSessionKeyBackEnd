package com.scheduler;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Login_info")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long AccountID;
	private String email;
	private String password;
	
	protected User() {}
	
	public User(String email, String pass) {
		this.email = email;
		this.password = pass;
	}
	
	@Override
	public String toString() {
		return String.format("User[id=%d, email=%s])", AccountID, email);
	}

	public Long getId() {
		return AccountID;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
}
