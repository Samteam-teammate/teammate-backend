package sejong.alom.teammate.domain.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.team.entity.Team;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
	Recruitment findByTeam(Team team);
}
