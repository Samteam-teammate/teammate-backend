package sejong.alom.teammate.domain.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.chat.dto.ChatMessageResponse;
import sejong.alom.teammate.domain.chat.dto.ChatRoomResponse;
import sejong.alom.teammate.domain.chat.entity.ChatMessage;
import sejong.alom.teammate.domain.chat.entity.ChatParticipant;
import sejong.alom.teammate.domain.chat.entity.ChatRoom;
import sejong.alom.teammate.domain.chat.repository.ChatMessageRepository;
import sejong.alom.teammate.domain.chat.repository.ChatParticipantRepository;
import sejong.alom.teammate.domain.chat.repository.ChatRoomRepository;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.repository.TeamRepository;
import sejong.alom.teammate.global.enums.ChatType;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChatParticipantRepository chatParticipantRepository;
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final ChatMessageRepository chatMessageRepository;

	@Transactional
	public Long getOrCreatePrivateRoom(Long myId, Long targetId) {
		Optional<ChatRoom> existingRoom = chatParticipantRepository.findPrivateRoomBetweenMembers(myId, targetId);
		if (existingRoom.isPresent()) {
			return existingRoom.get().getId();
		}

		Member me = memberRepository.getReferenceById(myId);
		Member target = memberRepository.getReferenceById(targetId);

		ChatRoom newRoom = ChatRoom.builder()
			.name(target.getName())
			.type(ChatType.PRIVATE)
			.build();
		chatRoomRepository.save(newRoom);

		chatParticipantRepository.save(ChatParticipant.builder().chatRoom(newRoom).member(me).build());
		chatParticipantRepository.save(ChatParticipant.builder().chatRoom(newRoom).member(target).build());

		return newRoom.getId();
	}

	@Transactional
	public Long getOrCreateTeamRoom(Long teamId, Long myId) {
		ChatRoom teamRoom = chatRoomRepository.findByTeamIdAndType(teamId, ChatType.TEAM)
			.orElseGet(() -> {
				Team team = teamRepository.findById(teamId).orElseThrow();
				return chatRoomRepository.save(
					ChatRoom.builder()
						.name(team.getName())
						.type(ChatType.TEAM)
						.teamId(teamId)
						.build());
			});

		// 내가 이 채팅방의 참여자로 등록되어 있는지 확인 후 없으면 추가
		boolean isParticipant = chatParticipantRepository.findAllByMemberId(myId).stream()
			.anyMatch(cp -> cp.getChatRoom().getId().equals(teamRoom.getId()));
		if (!isParticipant) {
			chatParticipantRepository.save(
				ChatParticipant.builder().chatRoom(teamRoom).member(memberRepository.getReferenceById(myId)).build()
			);
		}

		return teamRoom.getId();
	}

	@Transactional(readOnly = true)
	public Page<ChatRoomResponse> getMyChatRooms(Long memberId, Pageable pageable) {
		// 1. 내가 속한 채팅방 목록을 페이징하여 조회 (최신 메시지 순 정렬) -> 쿼리 1번
		Page<ChatParticipant> participantPage =
			chatParticipantRepository.findAllByMemberIdOrderByLatestMessage(memberId, pageable);

		if (participantPage.isEmpty()) {
			return Page.empty(pageable);
		}

		// 2. N+1 방지: 조회된 채팅방들의 ID만 추출
		List<Long> roomIds = participantPage.getContent().stream()
			.map(cp -> cp.getChatRoom().getId())
			.toList();

		// 3. 추출한 방 ID 목록으로 "각 방의 최신 메시지"를 단 한 번에 조회 -> 쿼리 2번
		List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessagesByRoomIds(roomIds);

		// 4. 빠른 매핑을 위해 Map으로 변환 (Key: roomId, Value: 최신 메시지 객체)
		Map<Long, ChatMessage> latestMessageMap = latestMessages.stream()
			.collect(Collectors.toMap(m -> m.getChatRoom().getId(), m -> m));

		// 5. 응답 DTO로 조립
		return participantPage.map(cp -> {
			ChatRoom room = cp.getChatRoom();
			ChatMessage latestMsg = latestMessageMap.get(room.getId());

			String lastMsgContent = null;
			LocalDateTime lastMsgAt = null;
			boolean hasNewMessage = false;

			// 최신 메시지가 존재하는 경우 안 읽음 여부 판별
			if (latestMsg != null) {
				lastMsgContent = latestMsg.getContent();
				lastMsgAt = latestMsg.getCreatedAt();

				// 한 번도 안 읽었거나, 마지막으로 읽은 시간보다 새 메시지의 시간이 더 뒤라면 새 메시지 있음
				if (cp.getLastReadAt() == null || latestMsg.getCreatedAt().isAfter(cp.getLastReadAt())) {
					hasNewMessage = true;
				}
			}

			return ChatRoomResponse.builder()
				.roomId(room.getId())
				.name(room.getName())
				.type(room.getType())
				.teamId(room.getTeamId())
				.lastMessage(lastMsgContent)
				.lastMessageAt(lastMsgAt)
				.hasNewMessage(hasNewMessage)
				.build();
		});
	}

	@Transactional
	public void updateLastReadTime(Long memberId, Long roomId) {
		ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndMemberId(roomId, memberId)
			.orElseThrow(() -> new IllegalArgumentException("해당 채팅방의 참여자가 아닙니다."));

		participant.updateLastReadAt();
	}

	@Transactional(readOnly = true)
	public Page<ChatMessageResponse> getMessages(Long roomId, Pageable pageable) {
		return chatMessageRepository.findAllByChatRoomIdOrderByCreatedAtDesc(roomId, pageable)
			.map(ChatMessageResponse::from);
	}
}
