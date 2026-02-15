package sejong.alom.teammate.domain.team.dto;

import sejong.alom.teammate.global.enums.TeamCategory;

public record TeamUpdateRequest(
	String name,
	String bio,
	TeamCategory category,
	String teamImage,
	Boolean isPublic,
	Integer maxMemberCount
) {
}
