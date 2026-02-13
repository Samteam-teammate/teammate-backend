package sejong.alom.teammate.domain.team.dto;

import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.enums.TeamCategory;

public record TeamCreateRequest(
	String name,
	String bio,
	TeamCategory category,
	String description,
	String teamImage,
	Boolean isPublic,
	Integer maxMemberCount
) {
	public Team to() {
		return Team.builder()
			.name(name)
			.bio(bio)
			.category(category)
			.description(description)
			.teamImage(teamImage)
			.isPublic(isPublic)
			.maxMemberCount(maxMemberCount)
			.currentMemberCount(1)
			.build();
	}
}
