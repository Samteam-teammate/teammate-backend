package sejong.alom.teammate.domain.recruitment.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.domain.recruitment.dto.ApplicantResponse;
import sejong.alom.teammate.domain.recruitment.dto.ApplyCreateRequest;
import sejong.alom.teammate.domain.recruitment.dto.MyApplyResponse;
import sejong.alom.teammate.domain.recruitment.entity.Apply;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.repository.ApplyRepository;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentRepository;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.entity.TeamMember;
import sejong.alom.teammate.domain.team.repository.TeamMemberRepository;
import sejong.alom.teammate.global.enums.ApplyStatus;
import sejong.alom.teammate.global.enums.TeamMemberRole;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Service
@RequiredArgsConstructor
public class ApplyService {
	private final RecruitmentRepository recruitmentRepository;
	private final TeamMemberRepository teamMemberRepository;
	private final ApplyRepository applyRepository;
	private final MemberRepository memberRepository;
	private final ProfileRepository profileRepository;

	@Transactional
	public void createApply(Long memberId, Long recruitmentId, ApplyCreateRequest request) {
		// 공고 조회
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
				.status(ApplyStatus.PENDING)
				.appliedPart(request.appliedPart())
				.description(request.description())
				.build()
		);
	}

	public Page<ApplicantResponse> getApplicants(Long recruitmentId, Pageable pageable) {
		// Apply와 Member 패치 조인
		Page<Apply> applyPage = applyRepository.findAllByRecruitmentIdWithMember(recruitmentId, pageable);

		// N+1 방지: 화면에 노출될 멤버들의 ID만 추출
		List<Long> applicantMemberIds = applyPage.getContent().stream()
			.map(apply -> apply.getMember().getId())
			.toList();

		// 프로필을 IN 쿼리로 한 번에 조회 후 Map으로 변환
		Map<Long, Profile> profileMap = profileRepository.findAllByMemberIdIn(applicantMemberIds).stream()
			.collect(Collectors.toMap(p -> p.getMember().getId(), p -> p));

		// DTO로 변환하여 반환
		return applyPage.map(apply -> {
			Profile profile = profileMap.get(apply.getMember().getId());
			if (profile == null) {
				throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND);
			}
			return ApplicantResponse.of(apply, profile);
		});
	}

	@Transactional
	public void decideApplyStatus(Long recruitmentId, Long applyId, ApplyStatus status) {
		// 지원 정보 조회
		Apply apply = applyRepository.findById(applyId)
			.orElseThrow(() -> new BusinessException(ErrorCode.APPLY_NOT_FOUND));
		Team team = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.RECRUITMENT_NOT_FOUND))
			.getTeam();

		apply.updateStatus(status);

		// 합격 시 팀원으로 편입
		if (status == ApplyStatus.ACCEPTED) {
			TeamMember newTeamMember = TeamMember.builder()
				.team(team)
				.member(apply.getMember())
				.role(TeamMemberRole.MEMBER)
				.part(apply.getAppliedPart())
				.build();
			teamMemberRepository.save(newTeamMember);
		}
	}

	public Page<MyApplyResponse> getMyApplies(Long memberId, Pageable pageable) {
		return applyRepository.findAllByMemberIdWithRecruitmentAndTeam(memberId, pageable)
			.map(MyApplyResponse::from);
	}
}
