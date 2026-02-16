package sejong.alom.teammate.domain.home.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.home.dto.HomeResponse;
import sejong.alom.teammate.domain.home.service.HomeService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Tag(name = "Home API", description = "홈 화면 관련 API 엔드포인트")
public class HomeController {
	private final HomeService homeService;

	@GetMapping
	@Operation(summary = "홈 화면 컨텐츠 조회", description = "임시: 최신순 모집 공고, 업데이트순 프로필, 마감 임박순 모집 공고 5개씩")
	public ResponseEntity<BaseResponse<HomeResponse>> getHomeContent(
		// @AuthenticationPrincipal User principal // TODO: 스크랩 추가하면 주석 해제
	) {
		// TODO: 내 프로필 혹은 내 팀 모집 공고 제외
		HomeResponse response = homeService.getHomeContent();

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("홈 화면 컨텐츠가 조회되었습니다.", response));
	}
}
