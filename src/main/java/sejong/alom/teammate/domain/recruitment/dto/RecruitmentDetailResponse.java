package sejong.alom.teammate.domain.recruitment.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.entity.RecruitmentPart;
import sejong.alom.teammate.domain.team.dto.TeamMemberResponse;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.TeamCategory;

@Builder
public record RecruitmentDetailResponse(
	String teamName,
	String teamImage,
	Integer maxMemberCount,
	Integer currentMemberCount,
	LocalDateTime deadline,
	TeamCategory category,
	List<Part> recruitmentParts,
	List<TeamMemberResponse> teamMembers,
	String description
) {
	public static RecruitmentDetailResponse of(Team team, Recruitment recruitment, List<TeamMemberResponse> teamMembers) {
		return RecruitmentDetailResponse.builder()
			.teamName(team.getName())
			.teamImage(team.getTeamImage())
			.maxMemberCount(team.getMaxMemberCount())
			.currentMemberCount(team.getCurrentMemberCount())
			.deadline(recruitment.getDeadline())
			.category(team.getCategory())
			.recruitmentParts(recruitment.getRecruitmentParts().stream()
				.map(RecruitmentPart::getPart).toList())
			.teamMembers(teamMembers)
			.description(recruitment.getDescription())
			.build();

	}
}
