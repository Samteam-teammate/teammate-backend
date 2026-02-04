package sejong.alom.teammate.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRegisterRequest(
	@NotBlank String name,
	@NotNull Long studentId,
	@NotBlank String nickname,
	String bio,
	String portfolioUrl,
	Boolean isOpenToWork,
	Boolean isVisible
) {
}
