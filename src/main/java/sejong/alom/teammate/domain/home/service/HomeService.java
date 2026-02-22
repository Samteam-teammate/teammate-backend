package sejong.alom.teammate.domain.home.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.home.dto.HomeResponse;
import sejong.alom.teammate.domain.member.dto.ProfileListFetchRequest;
import sejong.alom.teammate.domain.member.dto.ProfileListResponse;
import sejong.alom.teammate.domain.member.service.ProfileService;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentListFetchRequest;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentListResponse;
import sejong.alom.teammate.domain.recruitment.service.RecruitmentService;
import sejong.alom.teammate.global.enums.SortingType;

@Service
@RequiredArgsConstructor
public class HomeService {
	private final RecruitmentService recruitmentService;
	private final ProfileService profileService;

	@Transactional(readOnly = true)
	public HomeResponse getHomeContent(Long memberId) {
		Pageable pageable = PageRequest.of(0, 5);

		RecruitmentListFetchRequest popularRecruitmentConditions = RecruitmentListFetchRequest.builder()
			.sort(SortingType.LATEST) // TODO: 스크랩 추가 후 수정
			.isActive(true)
			.build();
		ProfileListFetchRequest popularProfilesConditions = ProfileListFetchRequest.builder()
			.sort(SortingType.LATEST)
			.build();
		RecruitmentListFetchRequest imminentRecruitmentConditions = RecruitmentListFetchRequest.builder()
			.sort(SortingType.IMMINENT)
			.isActive(true)
			.build();

		List<RecruitmentListResponse> popularRecruitments = recruitmentService.getRecruitmentList(
			memberId,
			popularRecruitmentConditions,
			pageable
		).getContent();
		List<ProfileListResponse> popularProfiles = profileService.getProfileList(
			memberId,
			popularProfilesConditions,
			pageable
		).getContent();
		List<RecruitmentListResponse> imminentRecruitments = recruitmentService.getRecruitmentList(
			memberId,
			imminentRecruitmentConditions,
			pageable
		).getContent();

		return HomeResponse.of(popularRecruitments, popularProfiles, imminentRecruitments);
	}
}
