package sejong.alom.teammate.domain.recruitment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.recruitment.dto.ApplyCreateRequest;
import sejong.alom.teammate.domain.recruitment.entity.Apply;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.repository.ApplyRepository;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentRepository;
import sejong.alom.teammate.domain.team.repository.TeamMemberRepository;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Service
@RequiredArgsConstructor
public class ApplyService {
	private final RecruitmentRepository recruitmentRepository;
	private final TeamMemberRepository teamMemberRepository;
	private final ApplyRepository applyRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public void createApply(Long memberId, Long recruitmentId, ApplyCreateRequest request) {
		// 공고 존재 확인
		Recruitment recruitment = recruitmentRepository.findWithTeamAndPartsById(recruitmentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.RECRUITMENT_NOT_FOUND));

		// 지원 파트 정합성 확인
		boolean isPartValid = recruitment.getRecruitmentParts().stream()
			.anyMatch(rp -> rp.getPart() == request.appliedPart());
		if (!isPartValid) {
			throw new BusinessException(ErrorCode.INVALID_APPLY_PART);
		}

		// 팀원 여부 확인
		if (teamMemberRepository.existsByTeamIdAndMemberId(recruitment.getTeam().getId(), memberId)) {
			throw new BusinessException(ErrorCode.ALREADY_TEAM_MEMBER);
		}

		// 지원 여부 확인
		if (applyRepository.existsByRecruitmentIdAndMemberId(recruitmentId, memberId)) {
			throw new BusinessException(ErrorCode.ALREADY_APPLIED);
		}

		// 지원
		Member member = memberRepository.getReferenceById(memberId);
		applyRepository.save(
			Apply.builder()
				.recruitment(recruitment)
				.member(member)
				.appliedPart(request.appliedPart())
				.description(request.description())
				.build()
		);
	}
}
