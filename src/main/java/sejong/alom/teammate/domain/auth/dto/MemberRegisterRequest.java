package sejong.alom.teammate.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.global.enums.MemberState;

public record MemberRegisterRequest(
	@NotBlank String tempToken,
	@NotBlank String name,
	@NotNull Long studentId,
	@NotBlank String nickname,
	String bio,
	String portfolioUrl,
	Boolean isOpenToWork,
	Boolean isVisible
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
			.build();
	}
}
