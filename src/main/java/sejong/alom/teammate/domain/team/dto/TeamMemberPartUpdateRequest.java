package sejong.alom.teammate.domain.team.dto;

import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.global.enums.Part;

public record TeamMemberPartUpdateRequest(
	@NotNull(message = "변경할 역할을 선택해주세요.")
	Part part
) {
}
