package sejong.alom.teammate.domain.recruitment.dto;

import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.global.enums.Part;

public record ApplyCreateRequest(
	@NotNull Part appliedPart,
	String description
) {

}
