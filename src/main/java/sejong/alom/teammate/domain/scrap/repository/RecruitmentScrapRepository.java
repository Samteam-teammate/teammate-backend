package sejong.alom.teammate.domain.scrap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.scrap.entity.RecruitmentScrap;

public interface RecruitmentScrapRepository extends JpaRepository<RecruitmentScrap, Long> {
	boolean existsByRecruitmentIdAndMemberId(Long teamId, Long memberId);
	Optional<RecruitmentScrap> findByRecruitmentIdAndMemberId(Long teamId, Long memberId);
}
