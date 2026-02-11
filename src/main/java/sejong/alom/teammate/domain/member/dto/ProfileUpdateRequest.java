package sejong.alom.teammate.domain.member.dto;

public record ProfileUpdateRequest(
	String nickname,
	String bio,
	String portfolioUrl,
	Boolean isOpenToWork,
	Boolean isVisible,
	String profileImage
) {
}
