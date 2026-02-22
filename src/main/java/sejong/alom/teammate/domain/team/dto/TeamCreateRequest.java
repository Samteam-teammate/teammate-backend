package sejong.alom.teammate.domain.team.dto;

import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.enums.TeamCategory;

public record TeamCreateRequest(
	String name,
	String bio,
	TeamCategory category,
	Boolean isPublic,
	Integer maxMemberCount
) {
	public Team to(String imageUrl) {
		return Team.builder()
			.name(name)
			.bio(bio)
			.category(category)
			.teamImage(imageUrl)
			.isPublic(isPublic)
			.maxMemberCount(maxMemberCount)
			.currentMemberCount(1)
			.build();
	}
}
