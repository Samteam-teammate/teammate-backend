package sejong.alom.teammate.domain.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.auth.dto.MemberLoginRequest;
import sejong.alom.teammate.domain.auth.dto.MemberRegisterRequest;
import sejong.alom.teammate.domain.auth.dto.SejongMemberDto;
import sejong.alom.teammate.domain.auth.dto.TokenDto;
import sejong.alom.teammate.domain.auth.provider.AuthTokenProvider;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.member.repository.ProfileRepository;
import sejong.alom.teammate.global.enums.MemberRole;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final MemberRepository memberRepository;
	private final ProfileRepository profileRepository;
	private final SejongPortalLoginService sejongPortalLoginService;
	private final AuthTokenProvider authTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional
	public TokenDto login(MemberLoginRequest request) {
		// 세종대 로그인
		SejongMemberDto memberInfo = sejongPortalLoginService.getMemberAuthInfos(String.valueOf(request.studentId()), request.password());

		// 회원 여부 확인 -> 회원이면 로그인 처리
		if (memberRepository.existsByStudentId(request.studentId())) {
			return issueToken(request.studentId());
		}

		// 그렇지 않으면 임시 토큰 발행 후 예외 처리 -> 임시 토큰은 사용자 정보와 함께 ResponseBody로 전달
		String tempToken = issueTempToken(request.studentId());
		Map<String, Object> data = new HashMap<>();
		data.put("tempToken", tempToken);
		data.put("studentId", memberInfo.studentId());
		data.put("name", memberInfo.name());

		throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND, data);
	}

	@Transactional
	public TokenDto register(MemberRegisterRequest request, MultipartFile profileImage) {
		// 임시 토큰 유효성 검사
		if (!authTokenProvider.isValidToken(request.tempToken())) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}
		String studentId = authTokenProvider.getStudentIdFromTempToken(request.tempToken());

		// 학번 일치 확인
		if (!studentId.equals(String.valueOf(request.studentId()))) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
		// 회원 여부 확인
		if (memberRepository.existsByStudentId(request.studentId())) {
			throw new BusinessException(ErrorCode.MEMBER_ALREADY_EXIST);
		}

		// TODO: 이미지 s3 업로드 후 String url 획득
		String image = null;

		// 새로운 member, profile 데이터 저장
		Member member = memberRepository.save(request.toMember());
		profileRepository.save(request.toProfile(member, image));

		// 회원가입 절차 완료 시 토큰 발행
		return issueToken(request.studentId());
	}

	@Transactional
	public void logout(String accessToken) {
		// access 토큰을 블랙리스트로 등록
		Long accessRemainingMs = authTokenProvider.getRemainingMs(accessToken);
		redisTemplate.opsForValue().set("auth:blacklist:" + accessToken, "logout", accessRemainingMs, TimeUnit.MILLISECONDS);

		// refresh 토큰을 redis에서 삭제
		redisTemplate.delete("auth:refresh:" + authTokenProvider.getSubject(accessToken));
	}

	@Transactional
	public TokenDto reissueToken(String refreshToken) {
		// refresh token 유효성 확인
		if (!authTokenProvider.isValidToken(refreshToken)) {
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}

		// memberId 추출, redis에 저장된 refresh token 획득
		long memberId = Long.parseLong(authTokenProvider.getSubject(refreshToken));
		String savedRefreshToken = redisTemplate.opsForValue().get("auth:refresh:" + memberId);
		log.info("saved refresh token: " + savedRefreshToken);
		log.info("request refresh token: " + refreshToken);

		// 기존과 동일한 토큰인지 확인
		if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}

		// 기존 토큰 삭제
		redisTemplate.delete("auth:refresh:" + memberId);

		// 새로운 토큰 발행, redis에 refresh 토큰 저장
		return issueToken(memberId);
	}

	private TokenDto issueToken(Long id) {
		Map<String, String> claims = new HashMap<>();
		claims.put("role", MemberRole.ROLE_USER.name());

		// 새로운 토큰 생성
		String accessToken = authTokenProvider.createAccessToken(id, claims);
		String refreshToken = authTokenProvider.createRefreshToken(id, claims);
		Long expiration = authTokenProvider.getRefreshExpirationSeconds();

		// redis에 refresh token 저장
		try {
			log.info("Redis 저장 시도 - Key: {}, Value: {}", "auth:refresh:" + id, refreshToken);
			redisTemplate.opsForValue()
				.set("auth:refresh:" + id, refreshToken, expiration * 1000L, TimeUnit.MILLISECONDS);

			String saved = redisTemplate.opsForValue().get("auth:refresh:" + id);
			log.info("저장 직후 확인: {}", saved);
		} catch (Exception e) {
			log.error("redis 저장 에러:" + e);
		}

		return TokenDto.of(accessToken, refreshToken, expiration);
	}

	private String issueTempToken(Long studentId) {
		Map<String, String> claims = new HashMap<>();
		claims.put("role", MemberRole.ROLE_GUEST.name());

		return authTokenProvider.createAccessToken(studentId, claims);
	}
}
