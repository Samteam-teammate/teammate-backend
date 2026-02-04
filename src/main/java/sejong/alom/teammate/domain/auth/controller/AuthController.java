package sejong.alom.teammate.domain.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.auth.dto.MemberLoginRequest;
import sejong.alom.teammate.domain.auth.dto.MemberRegisterRequest;
import sejong.alom.teammate.domain.auth.service.AuthService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증 관련 API 엔드포인트")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public BaseResponse<?> login(@Valid @RequestBody MemberLoginRequest request) {
		authService.login(request);
		return BaseResponse.success("로그인을 완료했습니다.");
	}

	@PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BaseResponse<?> signup(
		@Valid @RequestPart("profileInfo")MemberRegisterRequest request,
		@RequestPart("profileImage")MultipartFile profileImage
	) {
		// TODO: 임시 토큰으로 인증 절차를 거쳐 도달 가능
		authService.register(request, profileImage);
		return BaseResponse.success("회원가입을 완료했습니다.");
	}
}
