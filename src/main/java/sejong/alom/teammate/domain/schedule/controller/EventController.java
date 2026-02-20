package sejong.alom.teammate.domain.schedule.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.schedule.dto.EventResponse;
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
	public ResponseEntity<BaseResponse<?>> getEvents(
		@PathVariable Long teamId,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
	) {
		List<EventResponse> response = eventService.getEvents(teamId, startDate, endDate);
		return ResponseEntity.ok(BaseResponse.success("일정 목록이 조회되었습니다.", response));
	}

	@PostMapping
	@Operation(summary = "일정 생성")
	public ResponseEntity<BaseResponse<?>> createEvent(@PathVariable Long teamId) {
		return null;
	}

	@PatchMapping("/{eventId}")
	@Operation(summary = "일정 수정")
	public ResponseEntity<BaseResponse<?>> updateEvent(@PathVariable Long teamId, @PathVariable Long eventId) {
		return null;
	}

	@DeleteMapping("/{eventId}")
	@Operation(summary = "일정 삭제")
	public ResponseEntity<BaseResponse<?>> deleteEvent(@PathVariable Long teamId, @PathVariable Long eventId) {
		return null;
	}
}
