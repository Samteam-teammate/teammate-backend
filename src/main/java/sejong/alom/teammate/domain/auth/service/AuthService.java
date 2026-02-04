package sejong.alom.teammate.domain.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.chuseok22.sejongportallogin.core.SejongMemberInfo;
import com.chuseok22.sejongportallogin.infrastructure.SejongPortalLoginService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.auth.dto.MemberLoginRequest;
import sejong.alom.teammate.domain.auth.dto.MemberRegisterRequest;
import sejong.alom.teammate.domain.auth.dto.TokenDto;
import sejong.alom.teammate.domain.auth.provider.AuthTokenProvider;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.global.enums.MemberRole;
import sejong.alom.teammate.global.enums.MemberState;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final MemberRepository memberRepository;
	private final ProfileRepository profileRepository;
	private final SejongPortalLoginService sejongPortalLoginService;
	private final AuthTokenProvider authTokenProvider;

	@Transactional
	public TokenDto login(MemberLoginRequest request) {
		// 세종대 로그인
		SejongMemberInfo memberInfo = trySejongPortalLogin(request.studentId(), request.password());

		// 회원 여부 확인 -> 회원이면 로그인 처리
		if (memberRepository.existsByStudentId(request.studentId())) {
			return issueToken(request.studentId());
		}

		// 그렇지 않으면 임시 토큰 발행 후 에러 처리 -> 임시 토큰은 사용자 정보와 함께 ResponseBody로 전달
		// TODO: 에러 핸들링
		String tempToken = issueTempToken(request.studentId());
		throw new RuntimeException("404" + memberInfo.getName() + memberInfo.getStudentId() + tempToken);
	}

	@Transactional
	public TokenDto register(MemberRegisterRequest request, MultipartFile profileImage) {
		// 임시 토큰에서 학번 추출 후 검증
		String studentId = authTokenProvider.getStudentIdFromTempToken(request.tempToken());

		if (!studentId.equals(String.valueOf(request.studentId()))) {
			throw new RuntimeException("잘못된 접근입니다.");
		}
		if (memberRepository.existsByStudentId(request.studentId())) {
			throw new RuntimeException("이미 회원가입된 사용자입니다.");
		}

		// TODO: 이미지 s3 업로드 후 String url 획득
		String image = null;

		// 새로운 member, profile 데이터 저장
		// TODO: DTO toEntity 메서드 작성 고려
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

		// 회원가입 절차 완료 시 토큰 발행
		return issueToken(request.studentId());
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

	private TokenDto issueToken(Long id) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", MemberRole.ROLE_USER.name());

		String accessToken = authTokenProvider.createAccessToken(id, claims);
		String refreshToken = authTokenProvider.createRefreshToken(id, claims);
		Long expiration = authTokenProvider.getRefreshExpirationSeconds();

		return TokenDto.of(accessToken, refreshToken, expiration);
	}

	private String issueTempToken(Long studentId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", MemberRole.ROLE_GUEST.name());

		return authTokenProvider.createAccessToken(studentId, claims);
	}
}
