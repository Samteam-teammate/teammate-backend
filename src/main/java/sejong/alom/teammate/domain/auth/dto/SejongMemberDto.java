package sejong.alom.teammate.domain.auth.dto;

import lombok.Builder;

@Builder
public record SejongMemberDto(
	String major,
	String studentId,
	String name,
	String grade,
	String status,
	String completedSemester
) {
}
