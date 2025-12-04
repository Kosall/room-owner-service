package com.piseth.java.school.roomownerservice.service;

import com.piseth.java.school.roomownerservice.dto.PageDTO;
import com.piseth.java.school.roomownerservice.dto.RoomCreateRequest;
import com.piseth.java.school.roomownerservice.dto.RoomFilterDTO;
import com.piseth.java.school.roomownerservice.dto.RoomResponse;
import com.piseth.java.school.roomownerservice.dto.RoomUpdateRequest;

import reactor.core.publisher.Mono;

public interface RoomService {
	/*
	Mono<RoomDTO> createRoom(RoomDTO roomDTO);
	Mono<RoomDTO> getRoomById(String id);
	Mono<RoomDTO> updateRoom(String id, RoomDTO roomDTO);
	Mono<Void> deleteRoom(String id);
	Flux<RoomDTO> getRoomByFilter(RoomFilterDTO filterDTO);
	
	
	*/
	
	Mono<RoomResponse> create(RoomCreateRequest request, final String ownerId);
	Mono<RoomResponse> update(String id, RoomUpdateRequest request);
	Mono<Void> delete(String id);
	Mono<RoomResponse> getById(final String id);
	Mono<PageDTO<RoomResponse>> getRoomByFilterPagination( final RoomFilterDTO filterDTO);
	
	
}