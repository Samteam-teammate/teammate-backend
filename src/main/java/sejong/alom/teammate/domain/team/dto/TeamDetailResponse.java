package sejong.alom.teammate.domain.team.dto;

import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.enums.TeamCategory;

@Builder
public record TeamDetailResponse(
	String name,
	String bio,
	TeamCategory category,
	Integer maxMemberCount,
	Integer currentMemberCount,
	String teamImage,
	List<TeamMemberResponse> teamMember
) {
	public static TeamDetailResponse of(Team team, List<TeamMemberResponse> teamMember) {
		return TeamDetailResponse.builder()
			.name(team.getName())
			.bio(team.getBio())
			.category(team.getCategory())
			.maxMemberCount(team.getMaxMemberCount())
			.currentMemberCount(team.getCurrentMemberCount())
			.teamImage(team.getTeamImage())
			.teamMember(teamMember)
			.build();
	}
}
