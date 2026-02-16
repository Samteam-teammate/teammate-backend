package sejong.alom.teammate.domain.recruitment.dto;

import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.SortingType;
import sejong.alom.teammate.global.enums.TeamCategory;

@Builder
public record RecruitmentListFetchRequest(
	SortingType sort,
	List<TeamCategory> categories,
	List<Part> parts,
	Boolean isActive
) {
}
