package sejong.alom.teammate.domain.scrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.scrap.entity.ProfileScrap;

public interface ProfileScrapRepository extends JpaRepository<ProfileScrap, Long> {
	boolean existsByProfileIdAndMemberId(Long profileId, Long memberId);
}
