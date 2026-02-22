package sejong.alom.teammate.domain.chat.dto;

public record ChatMessageRequest(
	Long roomId,
	Long senderId,
	String content
) {
}
