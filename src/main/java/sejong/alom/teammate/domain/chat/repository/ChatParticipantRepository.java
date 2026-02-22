package sejong.alom.teammate.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sejong.alom.teammate.domain.chat.entity.ChatParticipant;
import sejong.alom.teammate.domain.chat.entity.ChatRoom;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
	@Query("select cp1.chatRoom from ChatParticipant cp1 " +
		"join ChatParticipant cp2 on cp1.chatRoom.id = cp2.chatRoom.id " +
		"where cp1.member.id = :memberId1 and cp2.member.id = :memberId2 " +
		"and cp1.chatRoom.type = 'PRIVATE'")
	Optional<ChatRoom> findPrivateRoomBetweenMembers(@Param("memberId1") Long member1, @Param("memberId2") Long member2);

	@Query("select cp from ChatParticipant cp join fetch cp.chatRoom where cp.member.id = :memberId")
	List<ChatParticipant> findAllByMemberId(@Param("memberId") Long memberId);

	@Query(value = "SELECT cp FROM ChatParticipant cp " +
		"LEFT JOIN ChatMessage cm ON cp.chatRoom = cm.chatRoom " +
		"WHERE cp.member.id = :memberId " +
		"GROUP BY cp " +
		"ORDER BY MAX(cm.createdAt) DESC",
		countQuery = "SELECT count(cp) FROM ChatParticipant cp WHERE cp.member.id = :memberId")
	Page<ChatParticipant> findAllByMemberIdOrderByLatestMessage(@Param("memberId") Long memberId, Pageable pageable);

	Optional<ChatParticipant> findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);
}
