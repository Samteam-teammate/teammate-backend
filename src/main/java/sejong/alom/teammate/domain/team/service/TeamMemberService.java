package sejong.alom.teammate.domain.team.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.domain.team.dto.TeamMemberResponse;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.entity.TeamMember;
import sejong.alom.teammate.domain.team.repository.TeamMemberRepository;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {
	private final TeamMemberRepository teamMemberRepository;
	private final ProfileRepository profileRepository;

	@Transactional(readOnly = true)
	public List<TeamMemberResponse> getTeamMemberList(Team team) {
		// 팀원과 멤버 엔티티 패치조인
		List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamWithMember(team);
		List<Long> memberIds = teamMembers.stream()
			.map(tm -> tm.getMember().getId())
			.toList();

		// 관련 프로필 한번에 조회
		Map<Long, Profile> profileMap = profileRepository.findAllByMemberIdIn(memberIds).stream()
			.collect(Collectors.toMap(p -> p.getMember().getId(), p -> p));

		// response로 변환 후 반환
		return teamMembers.stream()
			.map(tm -> {
				Profile profile = Optional.ofNullable(profileMap.get(tm.getMember().getId()))
					.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
				return TeamMemberResponse.of(tm, profile);
			})
			.toList();
	}
}
