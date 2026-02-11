package sejong.alom.teammate.domain.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
	private final ProfileRepository profileRepository;
	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public ProfileResponse getMyProfile(Long memberId) {
		log.info("요청 id: " + memberId);
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
			request.profileImage(),
			request.parts(),
			request.skills()
		);
	}

	@Transactional(readOnly = true)
	public Page<ProfileListResponse> getProfileList(ProfileListFetchRequest request, Pageable pageable) {
		Page<Profile> profilePage = profileRepository.searchProfiles(request, pageable);

		return profilePage.map(ProfileListResponse::from);
	}
}
