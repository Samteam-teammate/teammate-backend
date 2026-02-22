package sejong.alom.teammate.domain.member.dto;

import java.util.List;

import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;

public record ProfileUpdateRequest(
	String nickname,
	String bio,
	String portfolioUrl,
	Boolean isOpenToWork,
	Boolean isVisible,
	List<Part> parts,
	List<Skill> skills
) {
}
