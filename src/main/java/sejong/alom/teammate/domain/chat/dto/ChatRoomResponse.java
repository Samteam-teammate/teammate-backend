package sejong.alom.teammate.domain.chat.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import sejong.alom.teammate.domain.chat.entity.ChatRoom;
import sejong.alom.teammate.global.enums.ChatType;

@Builder
public record ChatRoomResponse(
	Long roomId,
	String name,
	ChatType type,
	Long teamId,
	String lastMessage,
	LocalDateTime lastMessageAt,
	Boolean hasNewMessage
) {
	public static ChatRoomResponse of(ChatRoom room, String lastMessage, LocalDateTime lastMessageAt, Boolean hasNewMessage) {
		return ChatRoomResponse.builder()
			.roomId(room.getId())
			.name(room.getName())
			.type(room.getType())
			.teamId(room.getTeamId())
			.lastMessage(lastMessage)
			.lastMessageAt(lastMessageAt)
			.hasNewMessage(hasNewMessage)
			.build();
	}
}
