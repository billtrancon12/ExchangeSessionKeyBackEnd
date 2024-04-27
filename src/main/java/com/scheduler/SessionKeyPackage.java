package com.scheduler;

public class SessionKeyPackage {
	private String secretKey;
	private String ivParameterSpec;
	private String signatureString;
	private String sessionID;
	
	public SessionKeyPackage(String secretKey, String ivParameterSpec, String signatureString, String sessionID) {
		this.secretKey = secretKey;
		this.ivParameterSpec = ivParameterSpec;
		this.signatureString = signatureString;
		this.sessionID = sessionID;
	}
	public String getSecretKey() {
		return this.secretKey;
	}
	public String getIvParameterSpec() {
		return this.ivParameterSpec;
	}
	public String getSignatureString() {
		return this.signatureString;
	}
	public String getSessionID() { return this.sessionID;}
}
