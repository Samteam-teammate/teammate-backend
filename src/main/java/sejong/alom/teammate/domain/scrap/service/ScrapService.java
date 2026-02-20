package sejong.alom.teammate.domain.scrap.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentRepository;
import sejong.alom.teammate.domain.scrap.dto.ScrappedRecruitmentResponse;
import sejong.alom.teammate.domain.scrap.entity.ProfileScrap;
import sejong.alom.teammate.domain.scrap.entity.RecruitmentScrap;
import sejong.alom.teammate.domain.scrap.repository.ProfileScrapRepository;
import sejong.alom.teammate.domain.scrap.repository.RecruitmentScrapRepository;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Service
@RequiredArgsConstructor
public class ScrapService {
	private final RecruitmentScrapRepository recruitmentScrapRepository;
	private final RecruitmentRepository recruitmentRepository;
	private final MemberRepository memberRepository;
	private final ProfileScrapRepository profileScrapRepository;
	private final ProfileRepository profileRepository;

	@Transactional
	public void createRecruitmentScrap(Long memberId, Long recruitmentId) {
		if (recruitmentScrapRepository.existsByRecruitmentIdAndMemberId(recruitmentId, memberId)) {
			throw new BusinessException(ErrorCode.ALREADY_SCRAPPED);
		}
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.RECRUITMENT_NOT_FOUND));
		Member member = memberRepository.getReferenceById(memberId); // Proxy 조회

		recruitmentScrapRepository.save(
			RecruitmentScrap.builder()
				.recruitment(recruitment)
				.member(member)
				.build()
		);
	}

	@Transactional
	public void deleteTeamScrap(Long memberId, Long recruitmentId) {
		RecruitmentScrap scrap = recruitmentScrapRepository.findByRecruitmentIdAndMemberId(recruitmentId, memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.SCRAP_NOT_FOUND));

		recruitmentScrapRepository.delete(scrap);
	}

	@Transactional(readOnly = true)
	public Page<ScrappedRecruitmentResponse> getScrappedRecruitments(Long memberId, Pageable pageable) {
		return recruitmentScrapRepository.findAllByMemberIdWithRecruitmentAndTeam(memberId, pageable)
			.map(ScrappedRecruitmentResponse::from);
	}

	@Transactional
	public void createProfileScrap(Long memberId, Long profileId) {
		if (profileScrapRepository.existsByProfileIdAndMemberId(profileId, memberId)) {
			throw new BusinessException(ErrorCode.ALREADY_SCRAPPED);
		}
		Profile profile = profileRepository.findById(profileId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
		Member member = memberRepository.getReferenceById(memberId);

		profileScrapRepository.save(
			ProfileScrap.builder()
				.profile(profile)
				.member(member)
				.build()
		);
	}

	@Transactional
	public void deleteProfileScrap(Long memberId, Long profileId) {
		ProfileScrap scrap = profileScrapRepository.findByProfileIdAndMemberId(profileId, memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.SCRAP_NOT_FOUND));

		profileScrapRepository.delete(scrap);
	}
}
