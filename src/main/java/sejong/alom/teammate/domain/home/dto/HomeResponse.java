package sejong.alom.teammate.domain.home.dto;

import java.util.List;

import lombok.Builder;
import sejong.alom.teammate.domain.member.dto.ProfileListResponse;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentListResponse;

@Builder
public record HomeResponse(
	List<RecruitmentListResponse> popularRecruitments,
	List<ProfileListResponse> popularProfiles,
	List<RecruitmentListResponse> imminentRecruitments
) {
	public static HomeResponse of(List<RecruitmentListResponse> pr, List<ProfileListResponse> pp, List<RecruitmentListResponse> ir) {
		return HomeResponse.builder()
			.popularRecruitments(pr)
			.popularProfiles(pp)
			.imminentRecruitments(ir)
			.build();
	}
}
