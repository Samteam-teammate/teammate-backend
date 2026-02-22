package sejong.alom.teammate.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
