package sejong.alom.teammate.domain.team.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.domain.team.dto.TeamCreateRequest;
import sejong.alom.teammate.domain.team.dto.TeamDetailResponse;
import sejong.alom.teammate.domain.team.dto.TeamListResponse;
import sejong.alom.teammate.domain.team.dto.TeamMemberResponse;
import sejong.alom.teammate.domain.team.dto.TeamUpdateRequest;
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
	private final ProfileRepository profileRepository;

	public void generateTeam(Long memberId, TeamCreateRequest request) {
		// 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		// 팀 생성 및 저장
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
		// 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		// 팀원인 데이터 찾아서 팀 추출
		List<TeamMember> teamMembers = teamMemberRepository.findAllByMember(member);
		return teamMembers.stream()
			.map(TeamMember::getTeam)
			.map(TeamListResponse::from)
			.toList();
	}

	public TeamDetailResponse getTeamInfo(Long teamId) {
		// 팀 조회
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 팀원과 멤버 엔티티 패치조인
		List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamWithMember(team);
		List<Long> memberIds = teamMembers.stream()
			.map(tm -> tm.getMember().getId())
			.toList();

		// 관련 프로필 한번에 조회
		Map<Long, Profile> profileMap = profileRepository.findAllByMemberIdIn(memberIds).stream()
			.collect(Collectors.toMap(p -> p.getMember().getId(), p -> p));

		// response로 변환
		List<TeamMemberResponse> teamMemberResponses = teamMembers.stream()
			.map(tm -> {
				Profile profile = Optional.ofNullable(profileMap.get(tm.getMember().getId()))
					.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
				return TeamMemberResponse.of(tm, profile);
			})
			.toList();

		return TeamDetailResponse.of(team, teamMemberResponses);
	}

	public void updateTeamInfo(Long teamId, TeamUpdateRequest request) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		team.update(
			request.name(),
			request.bio(),
			request.category(),
			request.description(),
			request.maxMemberCount(),
			request.teamImage(),
			request.isPublic()
		);
	}
}
