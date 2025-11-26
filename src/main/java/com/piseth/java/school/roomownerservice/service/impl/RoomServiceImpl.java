package com.piseth.java.school.roomownerservice.service.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piseth.java.school.roomownerservice.config.CorsConfig;
import com.piseth.java.school.roomownerservice.config.MessagingProperties;
import com.piseth.java.school.roomownerservice.config.OutboxProperties;
import com.piseth.java.school.roomownerservice.domain.Room;
import com.piseth.java.school.roomownerservice.domain.enumeration.RoomStatus;
import com.piseth.java.school.roomownerservice.dto.RoomCreateRequest;
import com.piseth.java.school.roomownerservice.dto.RoomResponse;
import com.piseth.java.school.roomownerservice.dto.RoomUpdateRequest;
import com.piseth.java.school.roomownerservice.exception.DefaultProplemDetailFactory;
import com.piseth.java.school.roomownerservice.exception.RoomNotFoundException;
import com.piseth.java.school.roomownerservice.mapper.RoomMapper;
import com.piseth.java.school.roomownerservice.messaging.events.RoomEventEnvelope;
import com.piseth.java.school.roomownerservice.messaging.events.RoomEventType;
import com.piseth.java.school.roomownerservice.messaging.events.RoomFullPayload;
import com.piseth.java.school.roomownerservice.outbox.OutboxEvent;
import com.piseth.java.school.roomownerservice.outbox.OutboxRepository;
import com.piseth.java.school.roomownerservice.outbox.OutboxStatus;
import com.piseth.java.school.roomownerservice.repository.RoomRepository;
import com.piseth.java.school.roomownerservice.service.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    private final DefaultProplemDetailFactory defaultProplemDetailFactory;

    private final CorsConfig corsConfig;
	private final RoomRepository repository;
	private final OutboxRepository outboxRepository;
	private final RoomMapper mapper;
	private final MessagingProperties msgProperties;
	private final OutboxProperties obxProperties;
	private final ObjectMapper objectMapper;



   
	@Override
	public Mono<RoomResponse> create(RoomCreateRequest request) {
		// save to db : room collection
		// outbox : outbox collection
		// filter from outbox to send kafka message

		final Room entity = mapper.toEntity(request);

		if (entity.getStatus() == null) {
			entity.setStatus(RoomStatus.AVAILABLE);
		}

		return repository.insert(entity)
				.flatMap(saved -> enqueueEvent(saved, RoomEventType.ROOM_CREATED, "create").thenReturn(saved))
				.map(mapper::toResponse)
				.doOnSuccess(r -> log.info("Room created id={}, ownerId={}", r.getId(), r.getOwnerId()))
				.doOnError(ex -> log.error("Create room failed: {}", ex.getMessage(), ex));

	}

	@Override
	public Mono<RoomResponse> update(String id, RoomUpdateRequest request) {
		return repository.findById(id).switchIfEmpty(Mono.error(new RoomNotFoundException(id))).flatMap(existing -> {
			mapper.updateEntity(existing, request);
			return repository.save(existing)
					.flatMap(saved -> enqueueEvent(saved, RoomEventType.ROOM_UPDATED, "update").thenReturn(saved));
		}).map(mapper::toResponse).doOnSuccess(r -> log.info("Room updated id={}", r.getId()))
				.doOnError(ex -> log.error("Update room failed: {}", ex.getMessage(), ex));

	}

	@Override
	public Mono<Void> delete(String id) {
		return repository.findById(id).switchIfEmpty(Mono.error(new RoomNotFoundException(id))).flatMap(existing -> {
			// keep final snapshot for delete event
			return enqueueEvent(existing, RoomEventType.ROOM_DELETED, "delete").then(repository.deleteById(id));
		}).doOnSuccess(v -> log.info("Room deleted id={}", id))
				.doOnError(ex -> log.error("Delete room failed: {}", ex.getMessage(), ex));
	}

	private Mono<OutboxEvent> enqueueEvent(final Room room, final RoomEventType type, final String actionHeader) {
		final RoomFullPayload payload = mapper.toFullPayload(room);

		final RoomEventEnvelope<RoomFullPayload> envelope = RoomEventEnvelope.of(type, msgProperties.getEventVersion(),
				msgProperties.getProducerId(), room.getId(), payload, Map.of("action", actionHeader));

		final String json;
		try {
			json = objectMapper.writeValueAsString(envelope);
		} catch (Exception e) {
			return Mono.error(new IllegalStateException("Failed to serialize event payload.", e));
		}

		final OutboxEvent evt = OutboxEvent.builder().aggregateId(room.getId()).aggregateType("Room")
				.eventType(type.name()).eventVersion(msgProperties.getEventVersion())
				.producer(msgProperties.getProducerId()).key(room.getId())
				.topic(msgProperties.getTopics().getRoomEvents()).headers(Map.of()).payloadJson(json)
				.occurredAt(LocalDateTime.now()).status(OutboxStatus.PENDING).attempt(0)
				.availableAt(LocalDateTime.now()).build();

		return outboxRepository.save(evt);
	}

	@Override
	public Mono<RoomResponse> getById(String id) {
		
		return repository.findById(id)
				.switchIfEmpty(Mono.error(new RoomNotFoundException(id)))
				.map(mapper::toResponse)
				.doOnSuccess(r->log.info("Patched room id :{}",id))
				.doOnError(ex->log.info("Fail to create room {}",ex.getMessage(),ex));
	}

}