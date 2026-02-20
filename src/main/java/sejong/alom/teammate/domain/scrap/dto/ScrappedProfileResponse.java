package sejong.alom.teammate.domain.scrap.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import sejong.alom.teammate.domain.scrap.entity.ProfileScrap;

@Builder
public record ScrappedProfileResponse(
	Long scrapId,
	Long profileId,
	String nickname,
	String bio,
	LocalDateTime scrappedAt
) {
	public static ScrappedProfileResponse from(ProfileScrap scrap) {
		return ScrappedProfileResponse.builder()
			.scrapId(scrap.getId())
			.profileId(scrap.getProfile().getId())
			.nickname(scrap.getProfile().getNickname())
			.bio(scrap.getProfile().getBio())
			.scrappedAt(scrap.getCreatedAt())
			.build();
	}
}
