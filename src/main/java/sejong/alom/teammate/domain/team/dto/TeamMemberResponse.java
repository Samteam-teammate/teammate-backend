package sejong.alom.teammate.domain.team.dto;

import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.team.entity.TeamMember;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;
import sejong.alom.teammate.global.enums.TeamMemberRole;

@Builder
public record TeamMemberResponse(
	Long memberId,
	String name,
	TeamMemberRole teamMemberRole,
	Part part,
	List<Skill> skills
) {
	public static TeamMemberResponse of(TeamMember teamMember, Profile profile) {
		return TeamMemberResponse.builder()
			.memberId(teamMember.getMember().getId())
			.name(profile.getNickname())
			.teamMemberRole(teamMember.getRole())
			.part(teamMember.getPart())
			.skills(profile.getProfileSkills())
			.build();
	}
}
