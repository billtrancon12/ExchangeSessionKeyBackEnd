package com.scheduler.services;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.stereotype.Service;


@Service
public class CryptographySymmetricService {
	private static CryptographySymmetricService instance;
	private static Cipher cipher;
	private SecretKey secretKey;
	private IvParameterSpec ivParameterSpec;
	
	private CryptographySymmetricService() {}
	
	private CryptographySymmetricService(String algorithm) throws Exception {
		cipher = Cipher.getInstance(algorithm);
	}
	
	public static CryptographySymmetricService getInstance(String algorithm) throws Exception {
		if(instance == null) {
			instance = new CryptographySymmetricService(algorithm);
		}
		else {
			cipher = Cipher.getInstance(algorithm);
		}
		return instance;
	}
	
	public void init(SecretKey secretKey, IvParameterSpec ivParameterSpec) throws Exception {
		this.secretKey = secretKey;
		this.ivParameterSpec = ivParameterSpec;
	}
	
	public String encrypt(String message) throws Exception{
		if(message.isBlank()) {
			throw new Exception("Empty message to encrypt");
		}
		cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, this.ivParameterSpec);
		byte[] encryptedBytes = cipher.doFinal(message.getBytes());
		String encodedEncryptedString = Base64.getEncoder().encodeToString(encryptedBytes);
		return encodedEncryptedString;
	}
	
	public String decrypt(String encryptedMesssage) throws Exception{
		if(encryptedMesssage.isBlank()) {
			throw new Exception("Empty messsage to decrypt");
		}
		cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.ivParameterSpec);
		byte[] plaintextBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMesssage));
		return new String(plaintextBytes);
	}
}
