package sejong.alom.teammate.domain.recruitment.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.entity.RecruitmentPart;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.TeamCategory;

@Builder
public record RecruitmentListResponse(
	Long recruitmentId,
	String teamName,
	String teamImage,
	TeamCategory category,
	String teamBio,
	List<Part> recruitmentParts,
	LocalDateTime deadline
) {
	public static RecruitmentListResponse from(Recruitment recruitment) {
		Team team = recruitment.getTeam();
		return RecruitmentListResponse.builder()
			.recruitmentId(recruitment.getId())
			.teamName(team.getName())
			.teamImage(team.getTeamImage())
			.category(team.getCategory())
			.teamBio(team.getBio())
			.recruitmentParts(recruitment.getRecruitmentParts().stream()
				.map(RecruitmentPart::getPart).toList())
			.deadline(recruitment.getDeadline())
			.build();

	}
}
