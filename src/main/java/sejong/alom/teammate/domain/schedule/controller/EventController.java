package sejong.alom.teammate.domain.schedule.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.schedule.dto.EventCreateRequest;
import sejong.alom.teammate.domain.schedule.dto.EventResponse;
import sejong.alom.teammate.domain.schedule.dto.EventUpdateRequest;
import sejong.alom.teammate.domain.schedule.service.EventService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/teams/{teamId}/events")
@RequiredArgsConstructor
@Tag(name = "Event API", description = "팀 일정 관련 API 엔드포인트")
public class EventController {
	private final EventService eventService;

	@GetMapping
	@Operation(summary = "일정 목록 조회", description = "날짜는 yyyy-MM-dd 형태로 받습니다")
	@PreAuthorize("@teamAuth.isTeamMember(#teamId, principal.username)")
	public ResponseEntity<BaseResponse<List<EventResponse>>> getEvents(
		@PathVariable Long teamId,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
	) {
		List<EventResponse> response = eventService.getEvents(teamId, startDate, endDate);
		return ResponseEntity.ok(BaseResponse.success("일정 목록이 조회되었습니다.", response));
	}

	@PostMapping
	@Operation(summary = "일정 생성")
	@PreAuthorize("@teamAuth.isTeamMember(#teamId, principal.username)")
	public ResponseEntity<BaseResponse<?>> createEvent(
		@PathVariable Long teamId,
		@Valid @RequestBody EventCreateRequest request
	) {
		Long eventId = eventService.createEvent(teamId, request);
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("일정이 생성되었습니다.", Map.of("eventId", eventId)));
	}

	@PatchMapping("/{eventId}")
	@Operation(summary = "일정 수정")
	@PreAuthorize("@teamAuth.isTeamMember(#teamId, principal.username)")
	public ResponseEntity<BaseResponse<?>> updateEvent(
		@PathVariable Long teamId,
		@PathVariable Long eventId,
		@Valid @RequestBody EventUpdateRequest request
	) {
		eventService.updateEvent(teamId, eventId, request);
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("일정이 수정되었습니다."));
	}

	@DeleteMapping("/{eventId}")
	@Operation(summary = "일정 삭제")
	@PreAuthorize("@teamAuth.isTeamMember(#teamId, principal.username)")
	public ResponseEntity<BaseResponse<?>> deleteEvent(@PathVariable Long teamId, @PathVariable Long eventId) {
		eventService.deleteEvent(teamId, eventId);
		return ResponseEntity.ok(BaseResponse.success("일정이 삭제되었습니다."));
	}
}
