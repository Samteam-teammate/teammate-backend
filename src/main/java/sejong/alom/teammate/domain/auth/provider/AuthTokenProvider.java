package sejong.alom.teammate.domain.auth.provider;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class AuthTokenProvider {
	@Value("${custom.jwt.secret-key}")
	private String secret;

	@Value("${custom.jwt.access-expire-seconds}")
	private Long jwtAccessExpirationSeconds;

	@Value("${custom.jwt.refresh-expire-seconds}")
	private Long jwtRefreshExpirationSeconds;

	private SecretKey secretKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	private String buildToken(String subject, long expireSeconds, Map<String, Object> claims) {
		Date issueAt = new Date();
		Date expiration = new Date(issueAt.getTime() + 1000L * expireSeconds);

		return Jwts.builder()
			.subject(subject)
			.issuedAt(issueAt)
			.expiration(expiration)
			.claims(claims)
			.signWith(secretKey())
			.compact();
	}

	public String createAccessToken(Long id, Map<String, Object> claims) {
		return buildToken(String.valueOf(id), jwtAccessExpirationSeconds, claims);
	}

	public String createRefreshToken(Long id, Map<String, Object> claims) {
		return buildToken(String.valueOf(id), jwtRefreshExpirationSeconds, claims);
	}

	public boolean isValidToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey())
				.build()
				.parse(token);
			return true;
		} catch (Exception e) {
			// TODO: 토큰 관련 에러 핸들링
			return false;
		}
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public String getSubject(String token) {
		return getClaims(token)
			.getSubject();
	}

	public Long getRemainingMs(String token) {
		return (System.currentTimeMillis() - getClaims(token).getExpiration().getTime());
	}

	public Long getRefreshExpirationSeconds() {
		return jwtRefreshExpirationSeconds;
	}

	public String getStudentIdFromTempToken(String token) {
		if (!isValidToken(token)) {
			throw new RuntimeException("잘못된 접근입니다.");
		}

		return getSubject(token);
	}
}
