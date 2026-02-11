package sejong.alom.teammate.domain.member.dto;

import lombok.Builder;
import sejong.alom.teammate.domain.member.entity.Profile;

@Builder
public record ProfileResponse(
	String nickname,
	String bio,
	String portfolioUrl,
	Boolean isOpenToWork,
	Boolean isVisible,
	String profileImage
) {
	public static ProfileResponse from(Profile profile) {
		return ProfileResponse.builder()
			.nickname(profile.getNickname())
			.bio(profile.getBio())
			.portfolioUrl(profile.getPortfolioUrl())
			.isOpenToWork(profile.getIsOpenToWork())
			.isVisible(profile.getIsVisible())
			.profileImage(profile.getProfileImage())
			.build();
	}
}
