package sejong.alom.teammate.domain.scrap.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentRepository;
import sejong.alom.teammate.domain.scrap.entity.RecruitmentScrap;
import sejong.alom.teammate.domain.scrap.repository.RecruitmentScrapRepository;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Service
@RequiredArgsConstructor
public class ScrapService {
	private final RecruitmentScrapRepository recruitmentScrapRepository;
	private final RecruitmentRepository recruitmentRepository;
	private final MemberRepository memberRepository;

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
}
