package sejong.alom.teammate.domain.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.chat.entity.ChatRoom;
import sejong.alom.teammate.global.enums.ChatType;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	Optional<ChatRoom> findByTeamIdAndType(Long teamId, ChatType type);
}
