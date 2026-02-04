package sejong.alom.teammate.domain.auth.dto;

public record MemberLoginRequest(
	Long studentId,
	String password
) {
}
