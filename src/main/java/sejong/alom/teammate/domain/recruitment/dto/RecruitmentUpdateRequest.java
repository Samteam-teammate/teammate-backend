package sejong.alom.teammate.domain.recruitment.dto;

import java.time.LocalDateTime;
import java.util.List;

import sejong.alom.teammate.global.enums.Part;

public record RecruitmentUpdateRequest(
	LocalDateTime deadline,
	String description,
	List<Part> recruitmentParts
) {
}
