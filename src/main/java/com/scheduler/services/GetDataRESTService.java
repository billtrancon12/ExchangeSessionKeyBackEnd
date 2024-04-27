package com.scheduler.services;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.scheduler.SessionKeyEntity;

@Service
public class GetDataRESTService {
	private String encryptedData;
	private String data;
	private String signature;
	private SecretKey secretKey;
	private IvParameterSpec iv;
	private SessionKeyEntity sessionKeyEntity;
	private final String MAC_ALGO = "HmacSHA256";
	private final String SYMMETRIC_ALGO = "AES/CFB/PKCS7Padding";
	
	public GetDataRESTService() {
		
	}
	
	public void init(String data, StoreSessionKeyService storeSessionKeyService) {
		JSONObject jsonObject = new JSONObject(data);
		this.encryptedData = jsonObject.getString("data");
		this.sessionKeyEntity = storeSessionKeyService.getSessionKeyEntity(jsonObject.getString(("id")));
		this.signature = jsonObject.getString("signature");
		this.data = "";
		this.secretKey = sessionKeyEntity.getSecretKey();
		this.iv = sessionKeyEntity.getIvParameterSpec();
	}
	
	private boolean verifyData() throws NoSuchAlgorithmException, InvalidKeyException {
		String hash = CryptographyMACService.hash(MAC_ALGO, secretKey, encryptedData);
		return hash.equals(signature);
	}

	private String decryptAndVerifyData() throws NoSuchAlgorithmException, Exception{
		if(!verifyData()) return "";
		
		CryptographySymmetricService cryptographySymmetricService = CryptographySymmetricService.getInstance(SYMMETRIC_ALGO);
		cryptographySymmetricService.init(secretKey, iv);
		this.data = cryptographySymmetricService.decrypt(encryptedData);
		return this.data;
	}
	
	public String getData() throws Exception{
		if(this.data.isEmpty()) {
			this.data = decryptAndVerifyData();
		}
		return this.data;
	}
}
