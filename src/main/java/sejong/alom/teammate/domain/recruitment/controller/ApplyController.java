package sejong.alom.teammate.domain.recruitment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
