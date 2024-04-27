package com.scheduler.services;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class CryptographyHashService {
	private MessageDigest mDigest;
	
	public CryptographyHashService() {}
	
	public CryptographyHashService(String hashAlgorithm) throws Exception{
		this.mDigest = MessageDigest.getInstance(hashAlgorithm);
	}
	
	public String hash(byte[] text) throws Exception{
		if(mDigest == null) {
			return null;
		}
		ByteArrayOutputStream temp = new ByteArrayOutputStream();
		temp.write(text);
		byte[] unhash = temp.toByteArray();
		byte[] messageHash = mDigest.digest(unhash);
		return Base64.getEncoder().encodeToString(messageHash); 
	}
}
