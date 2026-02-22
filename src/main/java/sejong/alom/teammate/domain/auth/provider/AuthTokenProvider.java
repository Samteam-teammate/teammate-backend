package sejong.alom.teammate.domain.auth.provider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import sejong.alom.teammate.global.enums.MemberRole;

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

	private String buildToken(String subject, long expireSeconds, Map<String, String> claims) {
		Date issueAt = new Date();
		Date expiration = new Date(issueAt.getTime() + 1000L * expireSeconds);

		return Jwts.builder()
			.claims(claims)
			.subject(subject)
			.issuedAt(issueAt)
			.expiration(expiration)
			.signWith(secretKey())
			.compact();
	}

	public String createAccessToken(Long id, Map<String, String> claims) {
		return buildToken(String.valueOf(id), jwtAccessExpirationSeconds, claims);
	}

	public String createRefreshToken(Long id, Map<String, String> claims) {
		return buildToken(String.valueOf(id), jwtRefreshExpirationSeconds, claims);
	}

	public void validateToken(String token) {
		Jwts.parser()
			.verifyWith(secretKey())
			.build()
			.parse(token);
	}

	public boolean isTempToken(String token) {
		return MemberRole.ROLE_GUEST.name()
			.equals(getClaims(token).get("role", String.class));
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
		return (getClaims(token).getExpiration().getTime() - System.currentTimeMillis());
	}

	public Long getRefreshExpirationSeconds() {
		return jwtRefreshExpirationSeconds;
	}

	public String getStudentIdFromTempToken(String token) {
		try {
			validateToken(token);
		} catch (Exception e) {
			throw new RuntimeException("잘못된 접근입니다.");
		}

		return getSubject(token);
	}

	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("role").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.toList();

		User principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}
}
