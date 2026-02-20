package sejong.alom.teammate.domain.scrap.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sejong.alom.teammate.domain.scrap.entity.RecruitmentScrap;

public interface RecruitmentScrapRepository extends JpaRepository<RecruitmentScrap, Long> {
	boolean existsByRecruitmentIdAndMemberId(Long teamId, Long memberId);
	Optional<RecruitmentScrap> findByRecruitmentIdAndMemberId(Long teamId, Long memberId);

	@Query(value = "select rs from RecruitmentScrap rs " +
		"join fetch rs.recruitment r " +
		"join fetch r.team " +
		"where rs.member.id = :memberId",
		countQuery = "select count(rs) from RecruitmentScrap rs where rs.member.id = :memberId")
	Page<RecruitmentScrap> findAllByMemberIdWithRecruitmentAndTeam(@Param("memberId") Long memberId, Pageable pageable);
}
