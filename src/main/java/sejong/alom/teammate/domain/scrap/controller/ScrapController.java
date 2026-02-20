package sejong.alom.teammate.domain.scrap.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.scrap.dto.ScrappedProfileResponse;
import sejong.alom.teammate.domain.scrap.dto.ScrappedRecruitmentResponse;
import sejong.alom.teammate.domain.scrap.service.ScrapService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
@Tag(name = "Scrap API", description = "스크랩 관련 API 엔드포인트")
public class ScrapController {

	private final ScrapService scrapService;

	@PostMapping("/recruitments/{recruitmentId}")
	@Operation(summary = "모집 공고 스크랩 생성")
	public ResponseEntity<BaseResponse<?>> scrapRecruitment(
		@AuthenticationPrincipal User principal,
		@PathVariable Long recruitmentId
	) {
		scrapService.createRecruitmentScrap(Long.parseLong(principal.getUsername()), recruitmentId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("모집 공고를 스크랩했습니다."));
	}

	@DeleteMapping("/recruitments/{recruitmentId}")
	@Operation(summary = "팀 스크랩 취소")
	public ResponseEntity<BaseResponse<?>> unscrapRecruitments(
		@AuthenticationPrincipal User principal,
		@PathVariable Long recruitmentId
	) {
		scrapService.deleteTeamScrap(Long.parseLong(principal.getUsername()), recruitmentId);
		return ResponseEntity.ok(BaseResponse.success("팀 스크랩이 취소되었습니다."));
	}

	@GetMapping("/recruitments")
	@Operation(summary = "스크랩한 팀 목록 조회")
	public ResponseEntity<BaseResponse<Page<ScrappedRecruitmentResponse>>> getScrappedTeams(
		@AuthenticationPrincipal User principal,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<ScrappedRecruitmentResponse> response =
			scrapService.getScrappedRecruitments(Long.parseLong(principal.getUsername()), pageable);
		return ResponseEntity.ok(BaseResponse.success("스크랩한 팀 목록을 조회했습니다.", response));
	}

	@PostMapping("/profiles/{profileId}")
	@Operation(summary = "프로필 스크랩 생성")
	public ResponseEntity<BaseResponse<?>> scrapProfile(
		@AuthenticationPrincipal User principal,
		@PathVariable Long profileId
	) {
		scrapService.createProfileScrap(Long.parseLong(principal.getUsername()), profileId);
		return ResponseEntity.status(HttpStatus.OK)
		 	.body(BaseResponse.success("프로필을 스크랩했습니다."));
	}

	@DeleteMapping("/profiles/{profileId}")
	@Operation(summary = "프로필 스크랩 취소")
	public ResponseEntity<BaseResponse<?>> unscrapProfile(
		@AuthenticationPrincipal User principal,
		@PathVariable Long profileId
	) {
		scrapService.deleteProfileScrap(Long.parseLong(principal.getUsername()), profileId);
		return ResponseEntity.ok(BaseResponse.success("프로필 스크랩이 취소되었습니다."));
	}

	@GetMapping("/profiles")
	@Operation(summary = "스크랩한 프로필 목록 조회")
	public ResponseEntity<BaseResponse<Page<ScrappedProfileResponse>>> getScrappedProfiles(
		@AuthenticationPrincipal User principal,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<ScrappedProfileResponse> response =
			scrapService.getScrappedProfiles(Long.parseLong(principal.getUsername()), pageable);
		return ResponseEntity.ok(BaseResponse.success("스크랩한 프로필 목록을 조회했습니다.", response));
	}
}
