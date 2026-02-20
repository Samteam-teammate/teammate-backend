package sejong.alom.teammate.domain.recruitment.dto;

import jakarta.validation.constraints.NotNull;
import sejong.alom.teammate.global.enums.ApplyStatus;

public record ApplyStatusUpdateRequest(
	@NotNull(message = "변경할 상태를 입력해주세요.")
	ApplyStatus status
) {

}