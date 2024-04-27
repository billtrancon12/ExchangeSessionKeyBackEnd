package com.scheduler.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.scheduler.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SignUpServiceUnitTest {

	@Autowired
	private SignUpService signUpService;
	
	@Test
	public void test_sign_up() {
		List<User> users = signUpService.list();
		assertEquals(users.size(), 3);
	}
}
