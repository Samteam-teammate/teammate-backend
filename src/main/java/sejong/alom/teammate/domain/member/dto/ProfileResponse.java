package sejong.alom.teammate.domain.member.dto;

import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;

@Builder
public record ProfileResponse(
	String nickname,
	String bio,
	String portfolioUrl,
	Boolean isOpenToWork,
	Boolean isVisible,
	String profileImage,
	List<Part> parts,
	List<Skill> skills
) {
	public static ProfileResponse from(Profile profile) {
		return ProfileResponse.builder()
			.nickname(profile.getNickname())
			.bio(profile.getBio())
			.portfolioUrl(profile.getPortfolioUrl())
			.isOpenToWork(profile.getIsOpenToWork())
			.isVisible(profile.getIsVisible())
			.profileImage(profile.getProfileImage())
			.parts(profile.getProfileParts())
			.skills(profile.getProfileSkills())
			.build();
	}
}
