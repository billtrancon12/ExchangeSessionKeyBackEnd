package com.scheduler.services;

import java.nio.charset.StandardCharsets;


import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.scheduler.exception.BadSignatureException;


// Only applicable if client has session key
@Service
public class ExchangeSessionKeyServiceClient {
	private CryptographyRSAService cryptographyRSAService;
	private String encryptedSessionKey;
	private String ivString;
	private String signatureString;
	
	public ExchangeSessionKeyServiceClient() throws Exception{
		this.cryptographyRSAService = new CryptographyRSAService();
	}
	
	public ExchangeSessionKeyServiceClient(String payload) throws Exception{
		this.cryptographyRSAService = new CryptographyRSAService();
		JSONObject jsonObject = new JSONObject(payload);
		this.encryptedSessionKey = jsonObject.getString("sessionKey");	
		this.ivString = jsonObject.getString("iv");
		this.signatureString = jsonObject.getString("signature");
	}
	
	public String getSessionKey(String privateKeyFilePath, String clientPublicKeyFilePath) throws Exception {
		if(privateKeyFilePath.isBlank() || encryptedSessionKey.isBlank() || clientPublicKeyFilePath.isBlank()) {
			return null;
		}
		String privateKeyString = ReadFileService.readFile(privateKeyFilePath);
		cryptographyRSAService.setPrivateKey(privateKeyString);
		String sessionKeyString = cryptographyRSAService.decryptWithPrivateKey(encryptedSessionKey);
		
		StringBuilder stringBuilder = new StringBuilder(encryptedSessionKey);
		stringBuilder.append(this.ivString);
		String tempString = stringBuilder.toString();
		
		if(!verifySessionKey(clientPublicKeyFilePath, signatureString, tempString)) {
			throw new BadSignatureException();
		}
		
		return sessionKeyString;	
	}
	
	public boolean verifySessionKey(String clientPublicKeyFilePath, String encryptedSignature, String sessionKeyWithIV) throws Exception {
		if(clientPublicKeyFilePath.isBlank() || this.signatureString.isBlank()) {
			return false;
		}
		String publicKeyString = ReadFileService.readFile(clientPublicKeyFilePath);
		cryptographyRSAService.setPublicKey(publicKeyString);
		String decryptedSignature = cryptographyRSAService.decryptWithPublicKey(encryptedSignature);
		
		CryptographyHashService cryptographyHashService = new CryptographyHashService("SHA-256");
		String hashSessionKeyAndIV = cryptographyHashService.hash(sessionKeyWithIV.getBytes(StandardCharsets.UTF_8));
		int paddingLength = decryptedSignature.length() - hashSessionKeyAndIV.length();
		
		// Checking the signature
		for(int i = hashSessionKeyAndIV.length() - 1; i >= 0; i--) {
			if(hashSessionKeyAndIV.charAt(i) != decryptedSignature.charAt(i + paddingLength)) return false;
		}
		
		return true;
	}
	
	public String getIVString() {
		return this.ivString;
	}
}
