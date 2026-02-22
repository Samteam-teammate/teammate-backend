package sejong.alom.teammate.global.filter;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.auth.provider.AuthTokenProvider;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final AuthTokenProvider authTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		// 토큰 추출
		String token = resolveToken(request);

		// 토큰 유효성 검사
		if (StringUtils.hasText(token)) {
			authTokenProvider.validateToken(token);

			// 로그아웃 여부를 확인하기 위해 redis 조회
			String isLogout = redisTemplate.opsForValue().get("auth:blacklist:" + token);

			if (isLogout == null) {
				// 임시 토큰이면 무시
				boolean isTempToken = authTokenProvider.isTempToken(token);

				if (!isTempToken) {
					Authentication authentication = authTokenProvider.getAuthentication(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}

		return null;
	}
}
