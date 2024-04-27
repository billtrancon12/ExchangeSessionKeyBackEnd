package com.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scheduler.SessionKeyEntity;

@Repository
public interface SessionKeyRepository extends JpaRepository<SessionKeyEntity, String>{
	public SessionKeyEntity findBySessionID(String sessionID);
}
