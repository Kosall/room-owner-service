package com.piseth.java.school.roomownerservice.service.security;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
@Service
public class DummyCurrentOwnerService implements CurrentOwnerIdService{

	@Override
	public Mono<String> getCurrentOwnerId() {
		// TODO Auto-generated method stub
		return Mono.just("123");
	}
	

}
