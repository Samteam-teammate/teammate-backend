package sejong.alom.teammate.domain.team.dto;

import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.global.enums.Part;

public record TeamMemberUpdateRequest(
	@NotNull(message = "역할을 선택해주세요.")
	Part part
) {
}
