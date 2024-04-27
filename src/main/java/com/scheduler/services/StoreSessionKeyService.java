package com.scheduler.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scheduler.SessionKeyEntity;
import com.scheduler.repository.SessionKeyRepository;

@Service
public class StoreSessionKeyService {
	@Autowired
	private SessionKeyRepository sessionKeyRepository;
	
	protected List<SessionKeyEntity> list(){
		return sessionKeyRepository.findAll();
	}
	
	public void storeSessionKey(String sessionID, String sessionKey, String iv) {
		SessionKeyEntity newSessionKeyEntity = new SessionKeyEntity(sessionID, sessionKey, iv);
		sessionKeyRepository.save(newSessionKeyEntity);
	}
	
	public SessionKeyEntity getSessionKeyEntity(String sessionID) {
		return sessionKeyRepository.findBySessionID(sessionID);
	}
}
