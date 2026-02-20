package sejong.alom.teammate.domain.schedule.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.domain.schedule.entity.Calendar;
import sejong.alom.teammate.domain.schedule.entity.Event;

public record EventCreateRequest(
	@NotBlank(message = "일정 제목을 입력해주세요.")
	String title,
	String description,
	@NotNull(message = "시작 시간을 입력해주세요.")
	LocalDateTime startTime,
	LocalDateTime endTime
) {
	public EventCreateRequest {
		if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
			throw new IllegalArgumentException("종료 시간이 시작 시간보다 빠를 수 없습니다.");
		}
	}

	public Event to(Calendar calendar) {
		return Event.builder()
			.title(this.title)
			.description(this.description)
			.startTime(this.startTime)
			.endTime(this.endTime)
			.calendar(calendar)
			.build();
	}
}
