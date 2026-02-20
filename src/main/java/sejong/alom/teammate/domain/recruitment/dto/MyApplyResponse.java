package sejong.alom.teammate.domain.recruitment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import sejong.alom.teammate.domain.recruitment.entity.Apply;
import sejong.alom.teammate.global.enums.ApplyStatus;

@Builder
public record MyApplyResponse(
	Long applyId,
	Long recruitmentId,
	String teamName,
	String description,
	ApplyStatus status,
	LocalDateTime appliedAt
) {
	public static MyApplyResponse from(Apply apply) {
		return MyApplyResponse.builder()
			.applyId(apply.getId())
			.recruitmentId(apply.getRecruitment().getId())
			.teamName(apply.getRecruitment().getTeam().getName())
			.description(apply.getDescription())
			.status(apply.getStatus())
			.appliedAt(apply.getCreatedAt())
			.build();
	}
}