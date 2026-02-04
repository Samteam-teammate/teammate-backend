package sejong.alom.teammate.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.chuseok22.sejongportallogin.core.SejongMemberInfo;
import com.chuseok22.sejongportallogin.infrastructure.SejongPortalLoginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.auth.dto.MemberLoginRequest;
import sejong.alom.teammate.domain.auth.dto.MemberRegisterRequest;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.global.enums.MemberState;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final MemberRepository memberRepository;
	private final ProfileRepository profileRepository;
	private final SejongPortalLoginService sejongPortalLoginService;

	@Transactional
	public void login(MemberLoginRequest request) {
		SejongMemberInfo memberInfo = trySejongPortalLogin(request.studentId(), request.password());

		if (memberRepository.existsByStudentId(request.studentId())) {
			// TODO: issueToken(); 로그인 처리를 위한 토큰 발행
		}

		throw new RuntimeException("404" + memberInfo.getName() + memberInfo.getStudentId());
		// TODO: issueToken(); 회원가입을 위한 임시 토큰 발행
	}

	@Transactional
	public void register(MemberRegisterRequest request, MultipartFile profileImage) {
		// TODO: 이미지 s3 업로드 후 String url 획득
		String image = null;

		if (memberRepository.existsByStudentId(request.studentId())) {
			throw new RuntimeException("이미 회원가입된 사용자입니다.");
		}

		Member member = memberRepository.save(
			Member.builder()
				.name(request.name())
				.studentId(request.studentId())
				.notificationSetting(true)
				.state(MemberState.ACTIVE)
				.build()
		);

		profileRepository.save(
			Profile.builder()
				.member(member)
				.nickname(request.nickname())
				.bio(request.bio())
				.portfolioUrl(request.portfolioUrl())
				.isOpenToWork(request.isOpenToWork())
				.isVisible(request.isVisible())
				.profileImage(image)
				.build()
		);

		// TODO: issueToken(); 로그인 처리를 위한 토큰 발행
	}

	private SejongMemberInfo trySejongPortalLogin(Long studentId, String password) {
		try {
			return sejongPortalLoginService.getMemberAuthInfos(String.valueOf(studentId), password);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("SEJONG_AUTH_DATA_FETCH_ERROR")) {
				throw new RuntimeException("아이디 또는 비밀번호를 잘못 입력하였습니다.");
			}
			throw new RuntimeException("일시적인 오류로 로그인을 할 수 없습니다. 잠시 후 다시 이용해주세요.");
		}
	}
}
