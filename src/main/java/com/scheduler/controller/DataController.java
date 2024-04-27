package com.scheduler.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.scheduler.SessionKeyPackage;
import com.scheduler.services.ExchangeSessionKeyFromSessionIDService;
import com.scheduler.services.ExchangeSessionKeyService;
import com.scheduler.services.GetDataRESTService;
import com.scheduler.services.SignUpService;
import com.scheduler.services.StoreSessionKeyService;


@RestController
public class DataController implements WebMvcConfigurer{
	@Autowired
	private SignUpService signUpService;
	@Autowired
	private StoreSessionKeyService storeSessionKeyService;
	@Autowired
	private GetDataRESTService getDataRESTService;

	@PostMapping("/api/signup")
	public ResponseEntity<String> signup(@RequestBody String data) throws Exception{
		getDataRESTService.init(data, storeSessionKeyService);
		signUpService.setNewUser(getDataRESTService.getData());
		HttpStatus status = signUpService.registerNewUser();
		
		switch(status) {
			case OK:
				return new ResponseEntity<String>("Success", status);
			case UNAUTHORIZED:
				return new ResponseEntity<String>("Username or password is not valid", status);
			case CONFLICT:
				return new ResponseEntity<String>("Username is already existed", status);
			default:
				return new ResponseEntity<String>("Something wrong", HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/api/exchange/sessionKeyWithSessionID")
	public ResponseEntity<SessionKeyPackage> retrieveAndSendSessionKey(@RequestBody String payload)throws Exception{
		JSONObject jsonObject = new JSONObject(payload);
		ExchangeSessionKeyFromSessionIDService exchangeSessionKeyFromSessionIDService = new ExchangeSessionKeyFromSessionIDService(jsonObject.getString("id"), storeSessionKeyService);
		ExchangeSessionKeyFromSessionIDService.initKey("C:\\musicblog\\src\\main\\resources\\clientPublicKey.pem.key", "C:\\musicblog\\src\\main\\resources\\private.pem.key");
		
		String encryptedSessionKey = exchangeSessionKeyFromSessionIDService.getEncryptedSecretKey();
		String encryptedIV = exchangeSessionKeyFromSessionIDService.getEncryptedIV();
		String signature = exchangeSessionKeyFromSessionIDService.getSelfSignedSignature();
		SessionKeyPackage responsePackage = new SessionKeyPackage(encryptedSessionKey, encryptedIV, signature, jsonObject.getString("id"));
		return new ResponseEntity<SessionKeyPackage>(responsePackage, HttpStatus.OK);
	}
	
	@PostMapping("/api/exchange/sessionKey")
	public ResponseEntity<SessionKeyPackage> exchangeSessionKey(@RequestBody String payload) throws Exception{
		ExchangeSessionKeyService exchangeSessionKeyService = new ExchangeSessionKeyService(payload, "C:\\musicblog\\src\\main\\resources\\private.pem.key", "C:\\musicblog\\src\\main\\resources\\clientPublicKey.pem.key");
		exchangeSessionKeyService.generateSessionKeyAndIV();
		
		String encryptedSessionKey = exchangeSessionKeyService.getEncryptedSecretKey();
		String encryptedIV = exchangeSessionKeyService.getEncryptedIvParameterSpec();
		String signatureString = exchangeSessionKeyService.getSelfSignSignature();
		String sessionID = exchangeSessionKeyService.getSessionID();
		SessionKeyPackage responsePackage = new SessionKeyPackage(encryptedSessionKey, encryptedIV, signatureString, sessionID);
		
		String secretKeyString = exchangeSessionKeyService.getSecretKeyString();
		String ivString = exchangeSessionKeyService.getIVSpecString();
		this.storeSessionKeyService.storeSessionKey(sessionID, secretKeyString, ivString);
		
		return new ResponseEntity<SessionKeyPackage>(responsePackage, HttpStatus.OK);
	}
	
	@Override 
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
	
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<String> unexpectedError(){
//		return new ResponseEntity<String>("Something wrong", HttpStatus.BAD_REQUEST);
//	}
}