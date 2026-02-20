package sejong.alom.teammate.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.recruitment.entity.Apply;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
	Boolean existsByRecruitmentIdAndMemberId(Long recruitmentId, Long memberId);
}
