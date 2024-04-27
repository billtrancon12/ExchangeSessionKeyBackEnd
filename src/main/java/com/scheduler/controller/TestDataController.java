package com.scheduler.controller;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestDataController {
	@PostMapping("/api/test/secretKey")
	public ResponseEntity<String> test(@RequestBody String payload) throws Exception{
		JSONObject jsonObject = new JSONObject(payload);
		String encryptedMessage = jsonObject.getString("message");
		String encodedKeyString = jsonObject.getString("secretKey");
		String encodedIvString = jsonObject.getString("iv");
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
		
		byte[] decodedKeyBytes = Base64.getDecoder().decode(encodedKeyString);
		SecretKey secretKey = new SecretKeySpec(decodedKeyBytes, 0, decodedKeyBytes.length, "AES");
		
		byte[] decodedIvBytes = Base64.getDecoder().decode(encodedIvString);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIvBytes);
		
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
		String decryptString = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)));
		System.out.println(decryptString);
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
}
