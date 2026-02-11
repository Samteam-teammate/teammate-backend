package sejong.alom.teammate.domain.member.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.member.dto.ProfileListFetchRequest;
import sejong.alom.teammate.domain.member.dto.ProfileListResponse;
import sejong.alom.teammate.domain.member.dto.ProfileResponse;
import sejong.alom.teammate.domain.member.dto.ProfileUpdateRequest;
import sejong.alom.teammate.domain.member.service.ProfileService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "사용자 정보 관련 API 엔드포인트")
public class ProfileController {
	private final ProfileService profileService;

	@GetMapping("/me")
	@Operation(summary = "내 프로필 조회")
	public ResponseEntity<BaseResponse<ProfileResponse>> getMyProfile(
		@AuthenticationPrincipal User principal
	) {
		ProfileResponse response = profileService.getMyProfile(Long.parseLong(principal.getUsername()));

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("프로필 조회가 완료되었습니다.", response));
	}

	@PatchMapping("/me")
	@Operation(summary = "내 프로필 수정")
	public ResponseEntity<BaseResponse<?>> updateMyProfile(
		@RequestBody ProfileUpdateRequest request,
		@AuthenticationPrincipal User principal
	) {
		profileService.updateMyProfile(Long.parseLong(principal.getUsername()), request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("프로필 수정이 완료되었습니다."));
	}

	@GetMapping("/profiles")
	@Operation(summary = "프로필 목록 조회")
	public ResponseEntity<BaseResponse<Page<ProfileListResponse>>> getProfileList(
		ProfileListFetchRequest request,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "size", required = false, defaultValue = "20") int size
		// @AuthenticationPrincipal User principal // TODO: Scrap 할 때 추가
	) {
		Pageable pageable = PageRequest.of(page, size);

		Page<ProfileListResponse> profileList = profileService.getProfileList(request, pageable);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("프로필 목록이 조회되었습니다.", profileList));
	}
}
