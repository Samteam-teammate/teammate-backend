package sejong.alom.teammate.domain.chat.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import sejong.alom.teammate.domain.chat.entity.ChatMessage;

@Builder
public record ChatMessageResponse(
	Long messageId,
	Long roomId,
	Long senderId,
	String content,
	LocalDateTime createdAt
) {
	public static ChatMessageResponse from(ChatMessage message) {
		return ChatMessageResponse.builder()
			.messageId(message.getId())
			.roomId(message.getChatRoom().getId())
			.senderId(message.getSenderId())
			.content(message.getContent())
			.createdAt(message.getCreatedAt())
			.build();
	}
}
