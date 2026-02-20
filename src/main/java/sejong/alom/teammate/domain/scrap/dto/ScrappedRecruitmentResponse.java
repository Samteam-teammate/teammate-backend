package sejong.alom.teammate.domain.scrap.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.scrap.entity.RecruitmentScrap;

@Builder
public record ScrappedRecruitmentResponse(
	Long scrapId,
	Long recruitmentId,
	String teamName,
	String description,
	LocalDateTime deadline,
	LocalDateTime scrappedAt
) {
	public static ScrappedRecruitmentResponse from(RecruitmentScrap scrap) {
		Recruitment recruitment = scrap.getRecruitment();
		return ScrappedRecruitmentResponse.builder()
			.scrapId(scrap.getId())
			.recruitmentId(recruitment.getId())
			.teamName(recruitment.getTeam().getName())
			.description(recruitment.getDescription())
			.deadline(recruitment.getDeadline())
			.scrappedAt(scrap.getCreatedAt())
			.build();
	}
}
