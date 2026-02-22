package sejong.alom.teammate.global.filter;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.global.exception.docs.ErrorCode;
import sejong.alom.teammate.global.util.BaseResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			setErrorResponse(response, ErrorCode.UNAUTHORIZED_ERROR, "만료된 토큰입니다.");
		} catch (JwtException | IllegalArgumentException e) {
			setErrorResponse(response, ErrorCode.UNAUTHORIZED_ERROR, "유효하지 않은 토큰입니다.");
		} catch (Exception e) {
			setErrorResponse(response, ErrorCode.SERVER_ERROR, "인증 처리 중 오류가 발생했습니다.");
		}
	}

	private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode, String customMessage) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		// BaseResponse 규격에 맞춰 JSON 생성
		BaseResponse<?> errorResponse = BaseResponse.fail(errorCode, customMessage != null ? customMessage : errorCode.getMessage());

		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}