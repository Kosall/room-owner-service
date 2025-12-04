package com.piseth.java.school.roomownerservice.service.security;

import reactor.core.publisher.Mono;

public interface CurrentOwnerIdService {
	Mono<String> getCurrentOwnerId();

}
