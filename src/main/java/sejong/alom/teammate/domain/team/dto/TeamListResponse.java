package sejong.alom.teammate.domain.team.dto;

import lombok.Builder;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.entity.TeamMember;
import sejong.alom.teammate.global.enums.TeamCategory;
import sejong.alom.teammate.global.enums.TeamMemberRole;

@Builder
public record TeamListResponse(
	Long teamId,
	String name,
	String bio,
	TeamCategory category,
	Integer currentMemberCount,
	String teamImage,
	TeamMemberRole role
) {
	public static TeamListResponse from(TeamMember teamMember) {
		Team team = teamMember.getTeam();

		return TeamListResponse.builder()
			.teamId(team.getId())
			.name(team.getName())
			.bio(team.getBio())
			.category(team.getCategory())
			.currentMemberCount(team.getCurrentMemberCount())
			.teamImage(team.getTeamImage())
			.role(teamMember.getRole())
			.build();
	}
}
