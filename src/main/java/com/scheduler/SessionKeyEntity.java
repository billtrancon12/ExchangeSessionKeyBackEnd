package com.scheduler;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Session_Key_Repository")
public class SessionKeyEntity {
	@Id
	private String sessionID;
	
	@Column(name = "key_value")
	private String keyValue;
	
	@Column(name = "iv")
	private String iv;
	
	public SessionKeyEntity() {}
	public SessionKeyEntity(String sessionID, String key_value, String iv) {
		this.sessionID = sessionID;
		this.keyValue = key_value;
		this.iv = iv;
	}
	
	@Override
	public String toString() {
		return String.format("SessionID: %s SessionKey: %s IV: %s", sessionID, keyValue, iv);
	}
	public String getSessionID() {
		return sessionID;
	}
	public String getKeyValueString() {
		return keyValue;
	}
	public String getIVString() {
		return iv;
	}
	
	public SecretKey getSecretKey() {
		byte[] decodedKey = Base64.getDecoder().decode(this.keyValue);
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return secretKey;
	}
	
	public IvParameterSpec getIvParameterSpec() {
		byte[] decodedIV = Base64.getDecoder().decode(this.iv);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIV, 0, decodedIV.length);
		return ivParameterSpec;
	}
}
