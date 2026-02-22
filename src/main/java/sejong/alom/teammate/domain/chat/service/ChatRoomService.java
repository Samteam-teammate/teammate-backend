package sejong.alom.teammate.domain.chat.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.chat.entity.ChatParticipant;
import sejong.alom.teammate.domain.chat.entity.ChatRoom;
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
		// TODO: myId가 해당 팀의 멤버인지 권한 검증 필요

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
}
