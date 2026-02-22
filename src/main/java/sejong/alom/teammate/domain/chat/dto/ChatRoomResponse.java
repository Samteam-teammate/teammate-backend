package sejong.alom.teammate.domain.chat.dto;

import lombok.Builder;
import sejong.alom.teammate.domain.chat.entity.ChatRoom;
import sejong.alom.teammate.global.enums.ChatType;

@Builder
public record ChatRoomResponse(
	Long roomId,
	String name,
	ChatType type,
	Long teamId
) {
	public static ChatRoomResponse from(ChatRoom room, String dynamicName) {
		return ChatRoomResponse.builder()
			.roomId(room.getId())
			.name(dynamicName != null ? dynamicName : room.getName())
			.type(room.getType())
			.teamId(room.getTeamId())
			.build();
	}
}
