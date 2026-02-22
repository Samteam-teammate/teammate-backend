package sejong.alom.teammate.domain.chat.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
	public ResponseEntity<BaseResponse<Page<ChatRoomResponse>>> getMyRooms(
		@AuthenticationPrincipal User principal,
		@PageableDefault(size = 20) Pageable pageable
	) {
		Page<ChatRoomResponse> response = chatRoomService.getMyChatRooms(Long.parseLong(principal.getUsername()), pageable);

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

	@PatchMapping("/{roomId}/read")
	@Operation(summary = "채팅방 읽음 처리", description = "안 읽음 상태를 해제하기 위해 채팅방 입장 시 호출이 필요합니다.")
	public ResponseEntity<BaseResponse<?>> updateLastReadTime(
		@PathVariable Long roomId,
		@AuthenticationPrincipal User principal) {
		chatRoomService.updateLastReadTime(Long.parseLong(principal.getUsername()), roomId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(BaseResponse.success("읽음 처리되었습니다."));
	}
}
