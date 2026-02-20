package sejong.alom.teammate.domain.recruitment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sejong.alom.teammate.domain.recruitment.entity.Apply;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
	Boolean existsByRecruitmentIdAndMemberId(Long recruitmentId, Long memberId);

	@Query(value = "select a from Apply a join fetch a.member where a.recruitment.id = :recruitmentId",
		countQuery = "select count(a) from Apply a where a.recruitment.id = :recruitmentId")
	Page<Apply> findAllByRecruitmentIdWithMember(Long recruitmentId, Pageable pageable);
}
