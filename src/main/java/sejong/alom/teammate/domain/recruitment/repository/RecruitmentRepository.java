package sejong.alom.teammate.domain.recruitment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.team.entity.Team;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
	Recruitment findByTeam(Team team);

	@Query("select r from Recruitment r " +
		"join fetch r.team t " +
		"left join fetch r.recruitmentParts " +
		"where r.id = :recruitmentId")
	Optional<Recruitment> findWithTeamAndPartsById(@Param("recruitmentId") Long recruitmentId);
}
