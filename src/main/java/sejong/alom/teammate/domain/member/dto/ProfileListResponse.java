package sejong.alom.teammate.domain.member.dto;

import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;

@Builder
public record ProfileListResponse(
	String nickname,
	String bio,
	Boolean isOpenToWork,
	String profileImage,
	List<Part> parts,
	List<Skill> skills
) {
	public static ProfileListResponse from(Profile profile) {
		return ProfileListResponse.builder()
			.nickname(profile.getNickname())
			.bio(profile.getBio())
			.isOpenToWork(profile.getIsOpenToWork())
			.profileImage(profile.getProfileImage())
			.parts(profile.getProfileParts())
			.skills(profile.getProfileSkills())
			.build();
	}
}
