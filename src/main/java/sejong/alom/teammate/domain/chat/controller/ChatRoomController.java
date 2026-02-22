package sejong.alom.teammate.domain.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.chat.service.ChatRoomService;
import sejong.alom.teammate.global.util.BaseResponse;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRoomController {
	private final ChatRoomService chatRoomService;

	@PostMapping("/private")
	@Operation(summary = "개인 채팅방 단일 조회")
	public ResponseEntity<BaseResponse<Long>> createPrivateRoom(
		@AuthenticationPrincipal User principal,
		@RequestParam Long targetId
	) {
		Long roomId = chatRoomService.getOrCreatePrivateRoom(Long.parseLong(principal.getUsername()), targetId);
		return ResponseEntity.ok(BaseResponse.success("개인 채팅방 반환", roomId));
	}

	@PostMapping("/team/{teamId}")
	@Operation(summary = "팀 채팅방 단일 조회")
	public ResponseEntity<BaseResponse<Long>> createTeamRoom(
		@AuthenticationPrincipal User principal,
		@PathVariable Long teamId
	) {
		Long roomId = chatRoomService.getOrCreateTeamRoom(teamId, Long.parseLong(principal.getUsername()));
		return ResponseEntity.ok(BaseResponse.success("팀 채팅방 반환", roomId));
	}
}
