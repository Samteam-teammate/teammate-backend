package sejong.alom.teammate.domain.auth.dto;

public record TokenDto(
	String accessToken,
	String refreshToken,
	Long refreshExpiration
) {
	public static TokenDto of(String accessToken, String refreshToken, Long expiration) {
		return new TokenDto(accessToken, refreshToken, expiration);
	}
}
