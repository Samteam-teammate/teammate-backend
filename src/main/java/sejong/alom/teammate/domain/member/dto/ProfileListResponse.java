package sejong.alom.teammate.domain.member.dto;

import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;

@Builder
public record ProfileListResponse(
	Long profileId,
	String nickname,
	String bio,
	Boolean isOpenToWork,
	String profileImage,
	List<Part> parts,
	List<Skill> skills
) {
	public static ProfileListResponse from(Profile profile) {
		return ProfileListResponse.builder()
			.profileId(profile.getId())
			.nickname(profile.getNickname())
			.bio(profile.getBio())
			.isOpenToWork(profile.getIsOpenToWork())
			.profileImage(profile.getProfileImage())
			.parts(profile.getProfileParts())
			.skills(profile.getProfileSkills())
			.build();
	}
}
