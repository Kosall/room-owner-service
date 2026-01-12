package com.piseth.java.school.roomownerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.piseth.java.school.roomownerservice.dto.PageDTO;
import com.piseth.java.school.roomownerservice.dto.RoomCreateRequest;
import com.piseth.java.school.roomownerservice.dto.RoomFilterDTO;
import com.piseth.java.school.roomownerservice.dto.RoomResponse;
import com.piseth.java.school.roomownerservice.dto.RoomUpdateRequest;
import com.piseth.java.school.roomownerservice.service.RoomService;
import com.piseth.java.school.roomownerservice.service.security.CurrentOwnerIdService;
import com.piseth.java.school.roomownerservice.service.security.DummyCurrentOwnerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
	private final RoomService roomService;
	private final CurrentOwnerIdService currentOwnerService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<RoomResponse> create(@Valid @RequestBody final RoomCreateRequest req) {
		return currentOwnerService.getCurrentOwnerId()
				.flatMap(ownerId->roomService.create(req, ownerId));
		//return roomService.create(req,"123");
	}

	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Mono<RoomResponse> update(@PathVariable final String id, @Valid @RequestBody final RoomUpdateRequest req) {
	 return currentOwnerService.getCurrentOwnerId()
		.flatMap(ownerId->roomService.update(id,req,ownerId));
	}
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable final String id){
		
		 return currentOwnerService.getCurrentOwnerId()
	    		  .flatMap(ownerId -> roomService.delete(id, ownerId));
	}
	@GetMapping("/{id}")
	  public Mono<RoomResponse> getById(@PathVariable final String id) {
	      return currentOwnerService.getCurrentOwnerId()
	    		  .flatMap(ownerId -> roomService.getRoomById(id, ownerId));
	  }
	 @GetMapping
	  public Mono<PageDTO<RoomResponse>> getRoomByFilterPagination(final RoomFilterDTO roomFilterDTO) {
	      return currentOwnerService.getCurrentOwnerId()
	    		  .flatMap(ownerId -> roomService.getRoomByFilterPagination(roomFilterDTO, ownerId));
	  }

}
