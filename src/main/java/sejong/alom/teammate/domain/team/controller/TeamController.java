package sejong.alom.teammate.domain.team.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.team.dto.TeamCreateRequest;
import sejong.alom.teammate.domain.team.dto.TeamListResponse;
import sejong.alom.teammate.domain.team.dto.TeamDetailResponse;
import sejong.alom.teammate.domain.team.dto.TeamUpdateRequest;
import sejong.alom.teammate.domain.team.service.TeamService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Team API", description = "팀 관련 API 엔드포인트")
public class TeamController {
	private final TeamService teamService;

	@PostMapping
	@Operation(summary = "팀 생성")
	public ResponseEntity<BaseResponse<?>> createTeam(
		@RequestBody TeamCreateRequest request,
		@AuthenticationPrincipal User principal
	) {
		teamService.generateTeam(Long.parseLong(principal.getUsername()), request);

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
	public ResponseEntity<BaseResponse<TeamDetailResponse>> getTeam(@PathVariable Long teamId) {
		TeamDetailResponse response = teamService.getTeamInfo(teamId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("팀이 조회되었습니다.", response));
	}

	@PatchMapping("/{teamId}")
	@Operation(summary = "팀 정보 수정")
	public ResponseEntity<BaseResponse<?>> updateTeam(
		@PathVariable Long teamId,
		@RequestBody TeamUpdateRequest request
	) {
		teamService.updateTeamInfo(teamId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("팀 정보가 수정되었습니다."));
	}
}
