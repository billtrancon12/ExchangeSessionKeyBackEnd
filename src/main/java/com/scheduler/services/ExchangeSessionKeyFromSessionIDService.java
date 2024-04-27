package com.scheduler.services;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.security.InvalidKeyException;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.scheduler.SessionKeyEntity;

@Service
public class ExchangeSessionKeyFromSessionIDService {
	private SessionKeyEntity sessionKeyEntity;
	
	private String encryptedSecretKey;
	private String encryptedIV;
	private String signature;
	
	private static String clientPublicKey = "";
	private static String serverPrivateKey = "";
	
	private static CryptographyRSAService cryptographyRSAService = null ;
	
	public ExchangeSessionKeyFromSessionIDService() {
		this.sessionKeyEntity = null;
		this.encryptedIV = "";
		this.encryptedSecretKey = "";
		this.signature = "";
	}
	
	public ExchangeSessionKeyFromSessionIDService(String sessionID, StoreSessionKeyService storeSessionKeyService) {
		this.sessionKeyEntity = storeSessionKeyService.getSessionKeyEntity(sessionID);
		this.encryptedIV = "";
		this.encryptedSecretKey = "";
		this.signature = "";	
	}
	
	// Initialize path for public key for client and private key for server
	public static void initKey(String clientPath, String privatePath) throws NoSuchFileException, FileNotFoundException, InvalidKeyException, Exception{
		if(clientPublicKey.isBlank()) {
			clientPublicKey = ReadFileService.readFile(clientPath);
			serverPrivateKey = ReadFileService.readFile(privatePath);
			cryptographyRSAService = new CryptographyRSAService();
			cryptographyRSAService.setPrivateKey(serverPrivateKey);
			cryptographyRSAService.setPublicKey(clientPublicKey);
		}
	}
	
	public static void resetKey() {
		clientPublicKey = "";
		serverPrivateKey = "";
		cryptographyRSAService = null;
	}
	
	public String getEncryptedSecretKey() throws Exception{
		if(this.sessionKeyEntity == null) return null;
		if(this.sessionKeyEntity.getKeyValueString().isBlank()) return null;
		if(this.encryptedSecretKey.isBlank()) {
			byte[] secretKeyBytes = this.sessionKeyEntity.getSecretKey().getEncoded();
			String encodedKey = Base64.getEncoder().encodeToString(secretKeyBytes);
			String encryptedKey = cryptographyRSAService.encryptWithPublicKey(encodedKey);
			this.encryptedSecretKey = encryptedKey;
		}
		return this.encryptedSecretKey;
	}
	
	public String getEncryptedIV() throws Exception{
		if(this.sessionKeyEntity == null) return null;
		if(this.sessionKeyEntity.getIVString().isBlank()) return null;
		if(this.encryptedIV.isBlank()) {
			byte[] ivBytes = this.sessionKeyEntity.getIvParameterSpec().getIV();
			String encodedIV = Base64.getEncoder().encodeToString(ivBytes);
			String encryptedIV = cryptographyRSAService.encryptWithPublicKey(encodedIV);
			this.encryptedIV = encryptedIV;
		}
		return this.encryptedIV;
	}
	
	public String getSelfSignedSignature() throws Exception{
		if(serverPrivateKey.isBlank()) return null;
		if(this.signature.isBlank()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.encryptedSecretKey);
			stringBuilder.append(this.encryptedIV);
			this.signature = CryptographyMACService.hash("HmacSHA256", this.sessionKeyEntity.getSecretKey(), stringBuilder.toString());
		}
		return this.signature;
	}
}
