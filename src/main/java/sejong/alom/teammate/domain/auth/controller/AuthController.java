package sejong.alom.teammate.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.auth.dto.MemberLoginRequest;
import sejong.alom.teammate.domain.auth.dto.MemberRegisterRequest;
import sejong.alom.teammate.domain.auth.dto.TokenDto;
import sejong.alom.teammate.domain.auth.service.AuthService;
import sejong.alom.teammate.global.util.BaseResponse;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증 관련 API 엔드포인트")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "로그인")
	public ResponseEntity<BaseResponse<?>> login(
		@Valid @RequestBody MemberLoginRequest request
	) {
		log.info("로그인 요청 학번: " + request.studentId());
		if (request.studentId()==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(BaseResponse.success("학번이 null로 들어오고 있습니다!!"));
		}

		// 로그인과 토큰 발행
		TokenDto dto = authService.login(request);

		// 응답 반환
		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + dto.accessToken())
			.header(HttpHeaders.SET_COOKIE, generateCookie(dto.refreshToken(), dto.refreshExpiration()))
			.body(BaseResponse.success("로그인 되었습니다.", "로그인 요청 학번: " + request.studentId()));
	}

	@PostMapping(value = "/signup"/*, consumes = MediaType.MULTIPART_FORM_DATA_VALUE*/)
	@Operation(summary = "회원가입 (프로필 사진 제외)")
	public ResponseEntity<BaseResponse<?>> signup(
		@Valid @RequestBody MemberRegisterRequest request
		//@RequestPart("profileImage")MultipartFile profileImage
	) {
		// DB에 회원 저장
		TokenDto dto = authService.register(request, null);

		// 응답 반환
		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + dto.accessToken())
			.header(HttpHeaders.SET_COOKIE, generateCookie(dto.refreshToken(), dto.refreshExpiration()))
			.body(BaseResponse.success("회원가입 되었습니다."));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<BaseResponse<?>> logout(
		@Parameter(hidden = true) @RequestHeader("Authorization") String authHeader
	) {
		// 로그아웃 - 토큰 관리
		authService.logout(authHeader.substring(7));

		// 응답 반환
		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.SET_COOKIE, generateCookie("", 0L))
			.body(BaseResponse.success("로그아웃 되었습니다."));
	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급")
	public ResponseEntity<BaseResponse<?>> refresh(
		@Parameter(hidden = true) @CookieValue(value = "refreshToken") String refreshToken) {
		// 토큰 재발행
		TokenDto dto = authService.reissueToken(refreshToken);

		// 응답 반환
		return ResponseEntity.status(HttpStatus.OK)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + dto.accessToken())
			.header(HttpHeaders.SET_COOKIE, generateCookie(dto.refreshToken(), dto.refreshExpiration()))
			.body(BaseResponse.success("토큰이 재발급되었습니다."));
	}

	private String generateCookie(String refreshToken, Long expiration) {
		return ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(expiration)
			.sameSite("None")
			.build()
			.toString();
	}
}
