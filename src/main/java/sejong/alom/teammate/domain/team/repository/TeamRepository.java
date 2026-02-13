package sejong.alom.teammate.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
