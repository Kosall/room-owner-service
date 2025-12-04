package com.piseth.java.school.roomownerservice.service.security;

import reactor.core.publisher.Mono;

public class DummyCurrentOwnerService implements CurrentOwnerIdService{

	@Override
	public Mono<String> getCurrentOwnerId() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
