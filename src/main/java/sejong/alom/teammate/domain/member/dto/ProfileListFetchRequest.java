package sejong.alom.teammate.domain.member.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;
import sejong.alom.teammate.global.enums.SortingType;

public record ProfileListFetchRequest(
	SortingType sort,
	List<Part> part,
	List<Skill> skill
) {
	@Builder
	public ProfileListFetchRequest {
		if (Objects.isNull(sort)) {
			sort = SortingType.RELEVANCE;
		}
	}
}
