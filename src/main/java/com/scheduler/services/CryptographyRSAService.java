package com.scheduler.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Service;


@Service
public class CryptographyRSAService {
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private Cipher encryptedCipher;
	private Cipher decryptedCipher;
	
	public CryptographyRSAService() throws NoSuchAlgorithmException, NoSuchPaddingException{
		this.encryptedCipher = Cipher.getInstance("RSA");
		this.decryptedCipher= Cipher.getInstance("RSA");
	}
	public CryptographyRSAService(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException{
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.encryptedCipher = Cipher.getInstance("RSA");
		this.decryptedCipher = Cipher.getInstance("RSA");
	}
	
	public void setPrivateKey(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException {
		privateKeyString = privateKeyString.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "");
		privateKeyString = privateKeyString.replaceAll("-----END RSA PRIVATE KEY-----", "");
		privateKeyString = privateKeyString.replaceAll("\\s", "");
		byte[] encodedBytes = Base64.getDecoder().decode(privateKeyString);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
		KeyFactory kFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = kFactory.generatePrivate(keySpec);
		this.privateKey = privateKey;
	}
	
	public void setPublicKey(String publicKeyString) throws Exception{
		publicKeyString = publicKeyString.replaceAll("-----BEGIN PUBLIC KEY-----", "");
		publicKeyString = publicKeyString.replaceAll("-----END PUBLIC KEY-----", "");
		publicKeyString = publicKeyString.replaceAll("\\s", "");
		byte[] encodeBytes = Base64.getDecoder().decode(publicKeyString);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodeBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		this.publicKey = publicKey;
	}
	
	public String decryptWithPrivateKey(String encryptedMessage) throws Exception {
		if(encryptedMessage.isBlank()) {
			throw new Exception("Empty encrypted message.");
		}
		if(this.privateKey == null) {
			return null;
		}
		decryptedCipher.init(Cipher.DECRYPT_MODE, this.privateKey);
		byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
		byte[] decryptedMessageBytes = decryptedCipher.doFinal(encryptedMessageBytes);
		return Base64.getEncoder().encodeToString(decryptedMessageBytes);
	}
	
	public String decryptWithPublicKey(String encryptedMessage) throws Exception{
		if(encryptedMessage.isBlank()) {
			throw new Exception("Empty encrypted message.");
		}
		if(this.publicKey == null) {
			return null;
		}
		decryptedCipher.init(Cipher.DECRYPT_MODE, this.publicKey);
		byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
		byte[] decryptedMessageBytes = decryptedCipher.doFinal(encryptedMessageBytes);
		return Base64.getEncoder().encodeToString(decryptedMessageBytes);
	}
	
	public String encryptWithPrivateKey(String plaintext) throws Exception {
		if(plaintext.isBlank()) {
			throw new Exception("Empty plaintext message.");
		}
		if(this.privateKey == null) {
			return null;
		}
		encryptedCipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
		byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
		byte[] encryptedMessageBytes = encryptedCipher.doFinal(plaintextBytes);
		return Base64.getEncoder().encodeToString(encryptedMessageBytes);
	}
	
	public String encryptWithPublicKey(String plaintext) throws Exception{
		if(plaintext.isBlank()) {
			throw new Exception("Empty plaintext message.");
		}
		if(this.publicKey == null) {
			return null;
		}
		encryptedCipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
		byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
		byte[] encryptedMessageBytes = encryptedCipher.doFinal(plaintextBytes);
		return Base64.getEncoder().encodeToString(encryptedMessageBytes);	
	}
}
