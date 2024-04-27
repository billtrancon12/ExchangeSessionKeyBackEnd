package com.scheduler.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

@Service
public class CryptographyMACService {	
	public CryptographyMACService() {}
	public static String hash(String algorithm, SecretKey secretKey, String data) throws NoSuchAlgorithmException, InvalidKeyException{
		Mac mac = Mac.getInstance(algorithm);
		mac.init(secretKey);
		return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
	}
	
}
