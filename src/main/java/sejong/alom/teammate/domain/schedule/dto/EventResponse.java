package sejong.alom.teammate.domain.schedule.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import sejong.alom.teammate.domain.schedule.entity.Event;

@Builder
public record EventResponse(
	Long eventId,
	String title,
	String description,
	LocalDateTime startTime,
	LocalDateTime endTime
) {
	public static EventResponse from(Event event) {
		return EventResponse.builder()
			.eventId(event.getId())
			.title(event.getTitle())
			.description(event.getDescription())
			.startTime(event.getStartTime())
			.endTime(event.getEndTime())
			.build();
	}
}
