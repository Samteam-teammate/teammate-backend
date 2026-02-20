package sejong.alom.teammate.domain.recruitment.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.recruitment.entity.Apply;
import sejong.alom.teammate.global.enums.ApplyStatus;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;

@Builder
public record ApplicantResponse(
	Long applyId,
	Long memberId,
	String nickname,
	Part appliedPart,
	List<Skill> skills,
	String description,
	ApplyStatus status,
	LocalDateTime appliedAt
) {
	public static ApplicantResponse of(Apply apply, Profile profile) {
		return ApplicantResponse.builder()
			.applyId(apply.getId())
			.memberId(apply.getMember().getId())
			.nickname(profile.getNickname())
			.appliedPart(apply.getAppliedPart())
			.skills(profile.getProfileSkills())
			.description(apply.getDescription())
			.status(apply.getStatus())
			.appliedAt(apply.getCreatedAt())
			.build();
	}
}
