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
import sejong.alom.teammate.global.util.S3Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final MemberRepository memberRepository;
	private final ProfileRepository profileRepository;
	//private final SejongPortalLoginService sejongPortalLoginService; // TODO: 로그인 정상화
	private final AuthTokenProvider authTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private final S3Service s3Service;

	@Transactional
	public TokenDto login(MemberLoginRequest request) {
		// 세종대 로그인
		//SejongMemberDto memberInfo = sejongPortalLoginService.getMemberAuthInfos(String.valueOf(request.studentId()), request.password());
		SejongMemberDto memberInfo = SejongMemberDto.builder()
			.studentId(String.valueOf(request.studentId()))
			.name("student")
			.grade("4")
			.major("학과")
			.status("상태")
			.completedSemester("재학")
			.build();

		// 회원 여부 확인
		Member member = memberRepository.findByStudentId(request.studentId())
			.orElseThrow(() -> {
				// 회원이 아니면 학번으로 임시 토큰 발행 후 예외 처리 -> 임시 토큰은 사용자 정보와 함께 ResponseBody로 전달
				String tempToken = issueTempToken(request.studentId());
				Map<String, Object> data = new HashMap<>();
				data.put("tempToken", tempToken);
				data.put("studentId", memberInfo.studentId());
				data.put("name", memberInfo.name());

				return new BusinessException(ErrorCode.MEMBER_NOT_FOUND, data);
			});

		// 회원이면 로그인 처리
		return issueToken(member.getId());
	}

	@Transactional
	public TokenDto register(MemberRegisterRequest request, MultipartFile profileImage) {
		// 임시 토큰 유효성 검사
		try {
			authTokenProvider.validateToken(request.tempToken());
		} catch (Exception e){
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}

		String studentId = authTokenProvider.getStudentIdFromTempToken(request.tempToken());

		// 임시 토큰의 학번 일치 확인
		if (!studentId.equals(String.valueOf(request.studentId()))) {
			throw new BusinessException(ErrorCode.INVALID_INPUT);
		}
		// 회원 여부 확인
		if (memberRepository.existsByStudentId(request.studentId())) {
			throw new BusinessException(ErrorCode.MEMBER_ALREADY_EXIST);
		}

		String imageUrl = null;
		if (profileImage != null && !profileImage.isEmpty()) {
			imageUrl = s3Service.upload(profileImage, "profiles");
		}

		// 새로운 member, profile 데이터 저장
		Member member = memberRepository.save(request.toMember());
		profileRepository.save(request.toProfile(member, imageUrl));

		// 회원가입 절차 완료 시 토큰 발행
		return issueToken(member.getId());
	}

	@Transactional
	public void logout(String accessToken) {
		// 유효하지 않은 토큰이어도 예외 처리하지 않음
		try {
			authTokenProvider.validateToken(accessToken);
		} catch (Exception e) {
			return;
		}

		// access 토큰을 블랙리스트로 등록
		Long accessRemainingMs = authTokenProvider.getRemainingMs(accessToken);
		redisTemplate.opsForValue().set("auth:blacklist:" + accessToken, "logout", accessRemainingMs, TimeUnit.MILLISECONDS);

		// refresh 토큰을 redis에서 삭제
		redisTemplate.delete("auth:refresh:" + authTokenProvider.getSubject(accessToken));
	}

	@Transactional
	public TokenDto reissueToken(String refreshToken) {
		// refresh token 유효성 확인
		try {
			authTokenProvider.validateToken(refreshToken);
		} catch (Exception e){
			throw new BusinessException(ErrorCode.INVALID_TOKEN);
		}

		// memberId 추출, redis에 저장된 refresh token 획득
		long memberId = Long.parseLong(authTokenProvider.getSubject(refreshToken));
		String savedRefreshToken = redisTemplate.opsForValue().get("auth:refresh:" + memberId);

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
			redisTemplate.opsForValue()
				.set("auth:refresh:" + id, refreshToken, expiration * 1000L, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.error("Redis error: " + e.getMessage());
			throw new BusinessException(ErrorCode.REDIS_ERROR);
		}

		return TokenDto.of(accessToken, refreshToken, expiration);
	}

	private String issueTempToken(Long studentId) {
		Map<String, String> claims = new HashMap<>();
		claims.put("role", MemberRole.ROLE_GUEST.name());

		return authTokenProvider.createAccessToken(studentId, claims);
	}
}
