package sejong.alom.teammate.domain.member.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.member.dto.ProfileListFetchRequest;
import sejong.alom.teammate.domain.member.dto.ProfileListResponse;
import sejong.alom.teammate.domain.member.dto.ProfileResponse;
import sejong.alom.teammate.domain.member.dto.ProfileUpdateRequest;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.domain.scrap.repository.ProfileScrapRepository;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;
import sejong.alom.teammate.global.util.S3Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
	private final ProfileRepository profileRepository;
	private final MemberRepository memberRepository;
	private final ProfileScrapRepository profileScrapRepository;
	private final S3Service s3Service;

	@Transactional(readOnly = true)
	public ProfileResponse getMyProfile(Long memberId) {
		// 요청 id로 member 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		// member로 profile 조회
		Profile profile = profileRepository.findByMember(member)
			.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

		return ProfileResponse.from(profile);
	}

	@Transactional
	public void updateMyProfile(Long memberId, ProfileUpdateRequest request) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		Profile profile = profileRepository.findByMember(member)
			.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

		profile.update(
			request.nickname(),
			request.bio(),
			request.portfolioUrl(),
			request.isOpenToWork(),
			request.isVisible(),
			request.parts(),
			request.skills()
		);
	}

	@Transactional
	public void updateProfileImage(Long memberId, MultipartFile file) {
		Profile profile = profileRepository.findByMemberId(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

		// 기존 이미지가 있다면 S3에서 삭제
		if (profile.getProfileImage() != null) {
			s3Service.delete(profile.getProfileImage());
		}

		// 새 이미지 업로드 후 URL 업데이트
		String imageUrl = s3Service.upload(file, "profiles");
		profile.updateImageUrl(imageUrl);
	}

	@Transactional(readOnly = true)
	public Page<ProfileListResponse> getProfileList(Long memberId, ProfileListFetchRequest request, Pageable pageable) {
		Page<Profile> profilePage = profileRepository.searchProfiles(request, pageable);

		if (memberId == null) {
			return profilePage.map(p -> ProfileListResponse.from(p, false));
		}

		List<Long> profileIds = profilePage.getContent().stream().map(Profile::getId).toList();
		Set<Long> scrappedIds = profileScrapRepository.findScrappedProfileIds(memberId, profileIds);

		return profilePage.map(p -> ProfileListResponse.from(p, scrappedIds.contains(p.getId())));
	}

	@Transactional(readOnly = true)
	public ProfileResponse getProfileDetail(Long profileId) {
		Profile profile = profileRepository.findById(profileId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

		return ProfileResponse.from(profile);
	}
}
