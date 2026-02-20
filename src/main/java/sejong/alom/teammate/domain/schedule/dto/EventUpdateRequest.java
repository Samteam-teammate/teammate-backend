package sejong.alom.teammate.domain.schedule.dto;

import java.time.LocalDateTime;

public record EventUpdateRequest(
	String title,
	String description,
	LocalDateTime startTime,
	LocalDateTime endTime
) {

}
