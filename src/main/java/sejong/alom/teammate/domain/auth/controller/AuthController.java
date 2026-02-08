package sejong.alom.teammate.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.auth.dto.MemberLoginRequest;
import sejong.alom.teammate.domain.auth.dto.MemberRegisterRequest;
import sejong.alom.teammate.domain.auth.dto.TokenDto;
import sejong.alom.teammate.domain.auth.service.AuthService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증 관련 API 엔드포인트")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "로그인 API")
	public ResponseEntity<BaseResponse<?>> login(
		@Valid @RequestBody MemberLoginRequest request,
		HttpServletResponse response
	) {
		// 로그인과 토큰 발행
		TokenDto dto = authService.login(request);

		// 헤더와 쿠키에 토큰 저장
		setTokenInResponse(response, dto);

		// 응답 반환
		return ResponseEntity.ok(BaseResponse.success("로그인 되었습니다."));
	}

	@PostMapping(value = "/signup"/*, consumes = MediaType.MULTIPART_FORM_DATA_VALUE*/)
	@Operation(summary = "회원가입 API (프로필 사진 제외)")
	public ResponseEntity<BaseResponse<?>> signup(
		@Valid @RequestBody MemberRegisterRequest request,
		//@RequestPart("profileImage")MultipartFile profileImage,
		HttpServletResponse response
	) {
		// DB에 회원 저장
		TokenDto dto = authService.register(request, null);

		// 헤더와 쿠키에 토큰 저장
		setTokenInResponse(response, dto);

		// 응답 반환
		return ResponseEntity.ok(BaseResponse.success("회원가입 되었습니다."));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃 API")
	public ResponseEntity<BaseResponse<?>> logout(
		@RequestHeader("Authorization") String authHeader,
		HttpServletResponse response
	) {
		// 로그아웃 - 토큰 관리
		authService.logout(authHeader.substring(7));

		// 쿠키에서 리프레시 토큰 삭제
		ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(0)
			.sameSite("None")
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok(BaseResponse.success("로그아웃 되었습니다."));
	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급 API")
	public ResponseEntity<BaseResponse<?>> refresh(
		@CookieValue(value = "refreshToken") String refreshToken,
		HttpServletResponse response
	) {
		// 토큰 재발행
		TokenDto dto = authService.reissueToken(refreshToken);

		// 쿠키와 헤더에 토큰 저장
		setTokenInResponse(response, dto);

		// 응답 반환
		return ResponseEntity.ok(BaseResponse.success("토큰이 재발급되었습니다."));
	}

	private void setTokenInResponse(HttpServletResponse response, TokenDto dto) {
		ResponseCookie cookie = ResponseCookie.from("refreshToken", dto.refreshToken())
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(dto.refreshExpiration())
			.sameSite("None")
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + dto.accessToken());
	}
}
