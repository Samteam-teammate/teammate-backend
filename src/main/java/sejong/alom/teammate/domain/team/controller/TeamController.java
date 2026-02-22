package sejong.alom.teammate.domain.team.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.team.dto.TeamCreateRequest;
import sejong.alom.teammate.domain.team.dto.TeamListResponse;
import sejong.alom.teammate.domain.team.dto.TeamDetailResponse;
import sejong.alom.teammate.domain.team.dto.TeamMemberUpdateRequest;
import sejong.alom.teammate.domain.team.dto.TeamUpdateRequest;
import sejong.alom.teammate.domain.team.service.TeamService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Team API", description = "팀 관련 API 엔드포인트")
public class TeamController {
	private final TeamService teamService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "팀 생성")
	public ResponseEntity<BaseResponse<?>> createTeam(
		@RequestPart("teamInfo") TeamCreateRequest request,
		@RequestPart(value = "teamImage", required = false) MultipartFile teamImage,
		@AuthenticationPrincipal User principal
	) {
		teamService.generateTeam(Long.parseLong(principal.getUsername()), request, teamImage);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("팀이 생성되었습니다."));
	}

	@GetMapping
	@Operation(summary = "내 팀 목록 조회")
	public ResponseEntity<BaseResponse<List<TeamListResponse>>> getMyTeamList(@AuthenticationPrincipal User principal) {
		List<TeamListResponse> teams = teamService.getMyTeamList(Long.parseLong(principal.getUsername()));

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("내 팀이 조회되었습니다.", teams));
	}

	@GetMapping("/{teamId}")
	@Operation(summary = "팀 상세 정보 조회")
	public ResponseEntity<BaseResponse<TeamDetailResponse>> getTeamDetail(@PathVariable Long teamId) {
		TeamDetailResponse response = teamService.getTeamDetail(teamId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("팀이 조회되었습니다.", response));
	}

	@PatchMapping("/{teamId}")
	@Operation(summary = "팀 정보 수정")
	@PreAuthorize("@teamAuth.isTeamLeader(#teamId, principal.username)")
	public ResponseEntity<BaseResponse<?>> updateTeam(
		@PathVariable Long teamId,
		@RequestBody TeamUpdateRequest request
	) {
		teamService.updateTeamInfo(teamId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("팀 정보가 수정되었습니다."));
	}

	@PatchMapping(value = "/{teamId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "팀 이미지 수정")
	@PreAuthorize("@teamAuth.isTeamLeader(#teamId, principal.username)")
	public ResponseEntity<BaseResponse<?>> updateTeamImage(
		@PathVariable Long teamId,
		@RequestPart("teamImage") MultipartFile file
	) {
		teamService.updateTeamImage(teamId, file);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("팀 이미지가 수정되었습니다."));
	}

	@PostMapping("/{teamId}/{memberId}")
	@Operation(summary = "(임시) member id로 팀원 추가")
	public ResponseEntity<BaseResponse<?>> addTeamMember(
		@PathVariable Long teamId,
		@PathVariable Long memberId,
		@RequestBody TeamMemberUpdateRequest request
	) {
		teamService.addTeamMember(teamId, memberId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("팀원이 추가되었습니다."));
	}

	@PatchMapping("/{teamId}/{memberId}")
	@Operation(summary = "팀원 역할 할당")
	@PreAuthorize("@teamAuth.isTeamLeader(#teamId, principal.username)")
	public ResponseEntity<BaseResponse<?>> updateTeamMemberPart(
		@PathVariable Long teamId,
		@PathVariable Long memberId,
		@RequestBody TeamMemberUpdateRequest request
	) {
		teamService.updateTeamMemberRole(teamId, memberId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("역할이 수정되었습니다."));
	}
}
