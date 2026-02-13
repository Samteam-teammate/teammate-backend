package sejong.alom.teammate.domain.team.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.team.dto.TeamCreateRequest;
import sejong.alom.teammate.domain.team.dto.TeamListResponse;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.entity.TeamMember;
import sejong.alom.teammate.domain.team.repository.TeamMemberRepository;
import sejong.alom.teammate.domain.team.repository.TeamRepository;
import sejong.alom.teammate.global.enums.TeamMemberRole;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final TeamMemberRepository teamMemberRepository;

	public void generateTeam(Long memberId, TeamCreateRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		Team team = teamRepository.save(request.to());
		teamMemberRepository.save(
			TeamMember.builder()
				.team(team)
				.member(member)
				.role(TeamMemberRole.LEADER)
				.build()
		);
	}

	public List<TeamListResponse> getMyTeamList(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		List<TeamMember> teamMembers = teamMemberRepository.findAllByMember(member);
		return teamMembers.stream()
			.map(TeamMember::getTeam)
			.map(TeamListResponse::from)
			.toList();
	}
}
