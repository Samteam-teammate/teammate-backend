package sejong.alom.teammate.domain.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.entity.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
	List<TeamMember> findAllByMember(Member member);
	Optional<TeamMember> findByTeamAndMember(Team team, Member member);
	Boolean existsByTeamIdAndMemberId(Long teamId, Long memberId);

	@Query("select tm from TeamMember tm join fetch tm.member m where tm.team = :team")
	List<TeamMember> findAllByTeamWithMember(@Param("team") Team team);
}
