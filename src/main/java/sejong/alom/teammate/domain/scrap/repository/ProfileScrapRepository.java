package sejong.alom.teammate.domain.scrap.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sejong.alom.teammate.domain.scrap.entity.ProfileScrap;

public interface ProfileScrapRepository extends JpaRepository<ProfileScrap, Long> {
	boolean existsByProfileIdAndMemberId(Long profileId, Long memberId);
	Optional<ProfileScrap> findByProfileIdAndMemberId(Long profileId, Long memberId);

	@Query(value = "select ps from ProfileScrap ps join fetch ps.profile where ps.member.id = :memberId",
		countQuery = "select count(ps) from ProfileScrap ps where ps.member.id = :memberId")
	Page<ProfileScrap> findAllByMemberIdWithProfile(@Param("memberId") Long memberId, Pageable pageable);

	@Query("select ps.profile.id from ProfileScrap ps " +
		"where ps.member.id = :memberId and ps.profile.id in :profileIds")
	Set<Long> findScrappedProfileIds(@Param("memberId") Long memberId, @Param("profileIds") List<Long> profileIds);
}
