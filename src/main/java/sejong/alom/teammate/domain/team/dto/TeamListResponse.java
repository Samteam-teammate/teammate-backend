package sejong.alom.teammate.domain.team.dto;

import lombok.Builder;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.enums.TeamCategory;

@Builder
public record TeamListResponse(
	Long teamId,
	String name,
	String bio,
	TeamCategory category,
	Integer currentMemberCount,
	String teamImage
) {
	public static TeamListResponse from(Team team) {
		return TeamListResponse.builder()
			.teamId(team.getId())
			.name(team.getName())
			.bio(team.getBio())
			.category(team.getCategory())
			.currentMemberCount(team.getCurrentMemberCount())
			.teamImage(team.getTeamImage())
			.build();
	}
}
