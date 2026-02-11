package sejong.alom.teammate.domain.member.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import sejong.alom.teammate.domain.meta.entity.Part;
import sejong.alom.teammate.domain.meta.entity.Skill;
import sejong.alom.teammate.global.enums.SortingType;
import sejong.alom.teammate.global.enums.TeamCategory;

public record ProfileListFetchRequest(
	SortingType sort,
	List<TeamCategory> category,
	List<Part> part,
	List<Skill> skill,
	Boolean isActive
) {
	@Builder
	public ProfileListFetchRequest {
		if (Objects.isNull(sort)) {
			sort = SortingType.RELEVANCE;
		}
	}
}
