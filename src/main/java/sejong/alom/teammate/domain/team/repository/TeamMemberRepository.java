package sejong.alom.teammate.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.team.entity.TeamMember;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
