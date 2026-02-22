package sejong.alom.teammate.domain.chat.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.chat.dto.ChatMessageRequest;
import sejong.alom.teammate.domain.chat.dto.ChatMessageResponse;
import sejong.alom.teammate.domain.chat.entity.ChatMessage;
import sejong.alom.teammate.domain.chat.entity.ChatRoom;
import sejong.alom.teammate.domain.chat.repository.ChatMessageRepository;
import sejong.alom.teammate.domain.chat.repository.ChatRoomRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;

	public ChatMessageResponse saveMessage(ChatMessageRequest request) {
		ChatRoom room = chatRoomRepository.findById(request.roomId())
			.orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

		ChatMessage message = ChatMessage.builder()
			.chatRoom(room)
			.senderId(request.senderId())
			.content(request.content())
			.build();
		chatMessageRepository.save(message);

		return ChatMessageResponse.from(message);
	}
}
