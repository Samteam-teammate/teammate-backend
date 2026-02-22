package sejong.alom.teammate.domain.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.chat.dto.ChatMessageRequest;
import sejong.alom.teammate.domain.chat.dto.ChatMessageResponse;
import sejong.alom.teammate.domain.chat.service.ChatMessageService;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessageService chatMessageService;

	@MessageMapping("/chat/message")
	public void sendMessage(ChatMessageRequest request) {
		// 1. DB에 메시지 저장
		ChatMessageResponse response = chatMessageService.saveMessage(request);

		// 2. 해당 채팅방을 구독(/topic/chat/room/{roomId})하고 있는 모든 클라이언트에게 메시지 브로드캐스트
		messagingTemplate.convertAndSend("/topic/chat/room/" + request.roomId(), response);
	}
}
