package sejong.alom.teammate.domain.recruitment.controller;

import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentCreateRequest;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentDetailResponse;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentListFetchRequest;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentListResponse;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentUpdateRequest;
import sejong.alom.teammate.domain.recruitment.service.RecruitmentService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
@Tag(name = "Recruitment API", description = "팀원 모집 공고 관련 API 엔드포인트")
public class RecruitmentController {
	// TODO: 배포 전에 DB 어떻게 할지 고민하고 올리기
	// 데이터 백업하고 아예 create 돌리거나..
	private final RecruitmentService recruitmentService;

	@PostMapping
	@Operation(summary = "모집 공고 생성")
	public ResponseEntity<BaseResponse<?>> createRecruitment(
		@RequestBody RecruitmentCreateRequest request
	) {
		Map<String, Long> response = recruitmentService.generateRecruitment(request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("모집 공고가 생성되었습니다.", response));
	}

	@PatchMapping("/{recruitmentsId}")
	@Operation(summary = "모집 공고 수정")
	public ResponseEntity<BaseResponse<?>> updateRecruitment(
		@PathVariable Long recruitmentsId,
		@RequestBody RecruitmentUpdateRequest request
	) {
		recruitmentService.updateRecruitment(recruitmentsId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("모집 공고가 수정되었습니다."));
	}

	@GetMapping("/{recruitmentId}")
	@Operation(summary = "모집 공고 상세 정보 조회")
	public ResponseEntity<BaseResponse<RecruitmentDetailResponse>> getRecruitmentDetail(
		@PathVariable Long recruitmentId
	) {
		return null;
	}

	@GetMapping
	@Operation(summary = "모집 공고 목록 조회")
	public ResponseEntity<BaseResponse<Page<RecruitmentListResponse>>> getProfileList(
		@ParameterObject @Valid RecruitmentListFetchRequest request,
		@RequestParam(value = "page", required = false, defaultValue = "0") int page,
		@RequestParam(value = "size", required = false, defaultValue = "20") int size
		// @AuthenticationPrincipal User principal // TODO: Scrap 할 때 추가
	) {
		Pageable pageable = PageRequest.of(page, size);

		//Page<ProfileListResponse> recruitmentList = recruitmentService.getProfileList(request, pageable);

		//return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("모집 공고 목록이 조회되었습니다.", recruitmentList));
		return null;
	}
}
