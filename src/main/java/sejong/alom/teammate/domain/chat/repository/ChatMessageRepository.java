package sejong.alom.teammate.domain.chat.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sejong.alom.teammate.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	Page<ChatMessage> findAllByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);

	@Query("SELECT m FROM ChatMessage m WHERE m.id IN " +
		"(SELECT MAX(m2.id) FROM ChatMessage m2 WHERE m2.chatRoom.id IN :roomIds GROUP BY m2.chatRoom.id)")
	List<ChatMessage> findLatestMessagesByRoomIds(@Param("roomIds") List<Long> roomIds);
}
