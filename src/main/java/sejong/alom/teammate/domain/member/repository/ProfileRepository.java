package sejong.alom.teammate.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long>, ProfileRepositoryCustom {
	Optional<Profile> findByMember(Member member);
	List<Profile> findAllByMemberIdIn(List<Long> memberIds);
}
