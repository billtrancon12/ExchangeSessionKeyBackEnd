package com.scheduler.services;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.scheduler.exception.BadSignatureException;


@Service
public class ExchangeSessionKeyService {	
	private CryptographyRSAService cryptographyRSAService;
	
	private String encryptedPingMessage;
	private String encryptedSignature;
	
	private SecretKey secretKey;
	private IvParameterSpec ivParameterSpec;
	
	private String clientPublicKeyPath;
	private String privateKeyPath;
	
	private String privateKeyString;
	private String clientPublicKeyString;
	
	private String encryptedSessionKey;
	private String encryptedIV;
	private String selfSignedSignature;
	
	private String sessionID;
	
	private final int SECRET_MESSAGE_LENGTH = 32;
	
	public ExchangeSessionKeyService() throws Exception{
		this.cryptographyRSAService = new CryptographyRSAService();
		this.encryptedPingMessage = "";
		this.encryptedSignature = "";
		this.clientPublicKeyPath = "";
		this.privateKeyPath = "";
		this.privateKeyString = "";
		this.clientPublicKeyString = "";
		this.encryptedSessionKey = "";
		this.encryptedIV = "";
		this.selfSignedSignature = "";
		this.secretKey = null;
		this.ivParameterSpec = null;
		this.sessionID = "";
	}
	public ExchangeSessionKeyService(String payload, String privatePath, String clientPath) throws Exception{
		this.clientPublicKeyPath = clientPath;
		this.privateKeyPath = privatePath;
		JSONObject jsonObject = new JSONObject(payload);
		this.encryptedPingMessage = jsonObject.getString("pingMessage");
		this.encryptedSignature = jsonObject.getString("signature");
		this.cryptographyRSAService = new CryptographyRSAService();
		this.privateKeyString = "";
		this.clientPublicKeyString = "";
		this.encryptedSessionKey = "";
		this.encryptedIV = "";
		this.selfSignedSignature = "";
		this.secretKey = null;
		this.sessionID = "";
		this.ivParameterSpec = null;
	}
	
	// Using server private key and client public key
	private String getPingMessage(String privateKeyPath, String clientPublicKeyPath) throws Exception {
		if(clientPublicKeyPath.isBlank() || privateKeyPath.isBlank()) {
			throw new Exception("Empty file path in ExchangeSessionKeyService");
		}
		if(this.encryptedPingMessage.isBlank() || this.encryptedSignature.isBlank()) {
			return null;
		}
		String clientPublicKeyString = ReadFileService.readFile(clientPublicKeyPath);
		this.clientPublicKeyString = clientPublicKeyString;
		String privateKeyString = ReadFileService.readFile(privateKeyPath);
		this.privateKeyString = privateKeyString;
		
		this.cryptographyRSAService.setPrivateKey(privateKeyString);
		String pingMessage = this.cryptographyRSAService.decryptWithPrivateKey(this.encryptedPingMessage);
		
		if(!verifySignature(clientPublicKeyString, this.encryptedSignature, this.encryptedPingMessage)) {
			throw new BadSignatureException();
		}
		
		return pingMessage;
	}
	
	
	private boolean verifySignature(String publicKeyString, String encryptedSignature, String pingMessage) throws Exception{
		if(publicKeyString.isBlank() || encryptedSignature.isBlank() || pingMessage.isBlank()) {
			return false;
		}
		this.cryptographyRSAService.setPublicKey(publicKeyString);
		String decryptedSignature = this.cryptographyRSAService.decryptWithPublicKey(encryptedSignature);
		
		CryptographyHashService cryptographyHashService = new CryptographyHashService("SHA-256");
		String hashPingMessage = cryptographyHashService.hash(pingMessage.getBytes(StandardCharsets.UTF_8));

		String temp1 = new String(Base64.getDecoder().decode(decryptedSignature.getBytes(StandardCharsets.UTF_8)));
		String temp2 = new String(Base64.getDecoder().decode(hashPingMessage.getBytes(StandardCharsets.UTF_8)));
		
		// Checking the signature
		int paddingLength = temp1.length() - temp2.length();
		for(int i = temp2.length() - 1; i >= 0; i--) {
			if(temp2.charAt(i) != temp1.charAt(i + paddingLength)) return false;
		}
		
		return true;
	}
	
	private void generateKey(String secret, String algorithm) throws NoSuchAlgorithmException{	
		byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
		this.secretKey = new SecretKeySpec(decoded, algorithm);
	}
	
	private void generateRandomIV() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		this.ivParameterSpec = new IvParameterSpec(iv);
	}
	
	// Generated plain session key and iv;
	public void generateSessionKeyAndIV() throws Exception {
		String pingMessage = new String(Base64.getDecoder().decode(getPingMessage(this.privateKeyPath, this.clientPublicKeyPath).getBytes(StandardCharsets.UTF_8)));
		if(pingMessage.equals("Exchange session key")) {
			String secretMessageEncoded = generateEncodedRandomSecret();
			generateKey(secretMessageEncoded, "AES");
			generateRandomIV();
		}
	}
	
	private String generateEncodedRandomSecret() {
		byte[] secretMessageBytes = new byte[this.SECRET_MESSAGE_LENGTH];
		for(int i = 0; i < this.SECRET_MESSAGE_LENGTH; i++) {
			secretMessageBytes[i] = (byte)Math.floor(Math.random() * 256);
		}
		byte[] secretMessageEncodedBytes = Base64.getEncoder().encode(secretMessageBytes);
		return new String(secretMessageEncodedBytes); 
	}
	
	// return the encrypted version of session key
	// encrypt with client public key
	public String getEncryptedSecretKey() throws Exception {
		if(this.secretKey == null) {
			throw new Exception("Empty secret key in getEncryptedSecretKey()");
		}
		if(this.encryptedSessionKey.isBlank()) {
			this.cryptographyRSAService.setPublicKey(clientPublicKeyString);
			String encodedKey = Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
			String encryptedKey = this.cryptographyRSAService.encryptWithPublicKey(encodedKey);
			this.encryptedSessionKey = encryptedKey;
			return encryptedKey;
		}else {
			return this.encryptedSessionKey;
		}
	}
	
	// return the encrypted version of iv
	// encrypt with client public key
	public String getEncryptedIvParameterSpec() throws Exception{
		if(this.ivParameterSpec == null) {
			throw new Exception("Empty IV in getEncryptedIVParameterSpec()");
		}
		if(this.encryptedIV.isBlank()) {
			this.cryptographyRSAService.setPublicKey(clientPublicKeyString);
			String encodedIV = Base64.getEncoder().encodeToString(this.ivParameterSpec.getIV());
			String encryptedIV = this.cryptographyRSAService.encryptWithPublicKey(encodedIV);
			this.encryptedIV = encryptedIV;
			return encryptedIV;
		}
		else {
			return this.encryptedIV;
		}
	}

	// Generate encrypted self signed signature with a hash value of session key and iv
	private void generateSelfSignedSignature() throws Exception{
		if(this.privateKeyString.isBlank()) {
			throw new Exception("Empty private key in generateSelfSignedSignature() in exchange session key service.");
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getEncryptedSecretKey());
		stringBuilder.append(getEncryptedIvParameterSpec());
		
		// Get the hash value of both encrypted session key and iv
		this.selfSignedSignature = CryptographyMACService.hash("HmacSHA256", this.secretKey, stringBuilder.toString());
	}
	
	// return the encrypted hash value of both session key and iv
	public String getSelfSignSignature() throws Exception {
		if(this.selfSignedSignature.isBlank()) {
			generateSelfSignedSignature();
			return this.selfSignedSignature;
		}
		return this.selfSignedSignature;
	}
	
	public SecretKey getSecretKey() {
		return this.secretKey;
	}
	
	public IvParameterSpec getIvParameterSpec(){
		return this.ivParameterSpec;
	}
	
	public String getSessionID() {
		if(this.sessionID.isBlank()) {
			this.sessionID = GenerateSessionIDService.generateSessionID();
			return this.sessionID;
		}
		return this.sessionID;
	}
	
	public String getSecretKeyString() {
		return Base64.getEncoder().encodeToString(this.secretKey.getEncoded());
	}
	
	public String getIVSpecString() {
		return Base64.getEncoder().encodeToString(this.ivParameterSpec.getIV());
	}
}
