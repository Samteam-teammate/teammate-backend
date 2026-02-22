package sejong.alom.teammate.domain.chat.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.chat.dto.ChatMessageResponse;
import sejong.alom.teammate.domain.chat.dto.ChatRoomResponse;
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

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("채팅방이 조회되었습니다.", roomId));
	}

	@PostMapping("/team/{teamId}")
	@Operation(summary = "팀 채팅방 단일 조회")
	public ResponseEntity<BaseResponse<Long>> createTeamRoom(
		@AuthenticationPrincipal User principal,
		@PathVariable Long teamId
	) {
		Long roomId = chatRoomService.getOrCreateTeamRoom(teamId, Long.parseLong(principal.getUsername()));

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("채팅방이 조회되었습니다.", roomId));
	}

	@GetMapping
	@Operation(summary = "채팅방 목록 조회")
	public ResponseEntity<BaseResponse<List<ChatRoomResponse>>> getMyRooms(@AuthenticationPrincipal User principal) {
		List<ChatRoomResponse> response = chatRoomService.getMyChatRooms(Long.parseLong(principal.getUsername()));

		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("채팅방 목록이 조회되었습니다.", response));
	}

	@GetMapping("/{roomId}/messages")
	@Operation(summary = "채팅 내역 조회", description = "특정 채팅방의 메세지 내역을 반환합니다.")
	public ResponseEntity<BaseResponse<Page<ChatMessageResponse>>> getMessages(
		@PathVariable Long roomId,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<ChatMessageResponse> response = chatRoomService.getMessages(roomId, pageable);

		return ResponseEntity.ok(BaseResponse.success("채팅 내역 조회", response));
	}
}
