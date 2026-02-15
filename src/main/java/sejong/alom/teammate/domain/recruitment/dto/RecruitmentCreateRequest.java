package sejong.alom.teammate.domain.recruitment.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.enums.Part;

public record RecruitmentCreateRequest(
	@NotNull Long teamId,
	LocalDateTime deadLine,
	List<Part> recruitParts,
	String description
) {
	public Recruitment to(Team team) {
		return Recruitment.builder()
			.team(team)
			.deadline(deadLine)
			.description(description)
			.build();
	}
}
