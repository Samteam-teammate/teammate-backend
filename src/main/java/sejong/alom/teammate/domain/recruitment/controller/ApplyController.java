package sejong.alom.teammate.domain.recruitment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.recruitment.dto.ApplicantResponse;
import sejong.alom.teammate.domain.recruitment.dto.ApplyCreateRequest;
import sejong.alom.teammate.domain.recruitment.service.ApplyService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Apply API", description = "모집 공고 지원 관련 API 엔드포인트")
public class ApplyController {
	private final ApplyService applyService;

	@PostMapping("/recruitments/{recruitmentId}/applies")
	@Operation(summary = "모집 공고 지원")
	public ResponseEntity<BaseResponse<?>> applyToRecruitment(
		@AuthenticationPrincipal User principal,
		@PathVariable Long recruitmentId,
		@Valid @RequestBody ApplyCreateRequest request
	) {
		applyService.createApply(Long.parseLong(principal.getUsername()), recruitmentId, request);
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("지원이 완료되었습니다."));
	}

	@GetMapping("/recruitments/{recruitmentId}/applies")
	@Operation(summary = "지원자 목록 조회")
	public ResponseEntity<BaseResponse<Page<ApplicantResponse>>> getApplicants(
		@AuthenticationPrincipal User principal,
		@PathVariable Long recruitmentId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Page<ApplicantResponse> response = applyService.getApplicants(Long.parseLong(principal.getUsername()), recruitmentId, pageable);
		return ResponseEntity.ok(BaseResponse.success("지원자 목록을 조회했습니다.", response));
	}
}
