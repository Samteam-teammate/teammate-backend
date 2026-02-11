package sejong.alom.teammate.domain.auth.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.global.enums.MemberState;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;

public record MemberRegisterRequest(
	@NotBlank String tempToken,
	@NotBlank String name,
	@NotNull Long studentId,
	@NotBlank String nickname,
	String bio,
	String portfolioUrl,
	Boolean isOpenToWork,
	Boolean isVisible,
	List<Part> parts,
	List<Skill> skills
) {
	public Member toMember() {
		return Member.builder()
			.name(name)
			.studentId(studentId)
			.notificationSetting(true)
			.state(MemberState.ACTIVE)
			.build();
	}

	public Profile toProfile(Member member, String profileImage) {
		return Profile.builder()
			.member(member)
			.nickname(nickname)
			.bio(bio)
			.portfolioUrl(portfolioUrl)
			.isOpenToWork(isOpenToWork)
			.isVisible(isVisible)
			.profileImage(profileImage)
			.profileParts(parts)
			.profileSkills(skills)
			.build();
	}
}
