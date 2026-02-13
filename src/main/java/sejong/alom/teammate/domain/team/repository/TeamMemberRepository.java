package sejong.alom.teammate.domain.team.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.team.entity.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
	List<TeamMember> findAllByMember(Member member);
}
