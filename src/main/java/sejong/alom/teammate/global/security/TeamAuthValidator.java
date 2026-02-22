package sejong.alom.teammate.global.security;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentRepository;
import sejong.alom.teammate.domain.team.repository.TeamMemberRepository;
import sejong.alom.teammate.global.enums.TeamMemberRole;

@Component("teamAuth") // SpEL에서 @teamAuth 로 호출하기 위해 이름 지정
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamAuthValidator {

	private final TeamMemberRepository teamMemberRepository;
	private final RecruitmentRepository recruitmentRepository;

	// 해당 팀의 소속 팀원인지 확인
	public boolean isTeamMember(Long teamId, String principalUsername) {
		Long memberId = Long.parseLong(principalUsername);
		return teamMemberRepository.existsByTeamIdAndMemberId(teamId, memberId);
	}

	// 해당 팀의 팀장인지 확인
	public boolean isTeamLeader(Long teamId, String principalUsername) {
		Long memberId = Long.parseLong(principalUsername);
		return teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId)
			.map(teamMember -> teamMember.getRole() == TeamMemberRole.LEADER)
			.orElse(false);
	}

	// 모집 공고 ID를 기반으로 해당 팀의 팀장인지 확인 -> 합불 결정에 사용
	public boolean isLeaderByRecruitment(Long recruitmentId, String principalUsername) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElse(null);
		if (recruitment == null) return false;

		return isTeamLeader(recruitment.getTeam().getId(), principalUsername);
	}

	// 모집 공고 ID를 기반으로 해당 팀의 팀원인지 확인 -> 지원자 목록 조회에 사용
	public boolean isMemberByRecruitment(Long recruitmentId, String principalUsername) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElse(null);
		if (recruitment == null) return false;

		return isTeamMember(recruitment.getTeam().getId(), principalUsername);
	}
}
