package sejong.alom.teammate.global.exception.docs;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	/*
	E 공통
	J Jwt
	A Auth
	M Member
	P Profile
	T Team
	S Scrap
	U Upstream
	D db, redis
	 */

	// 400 Bad Request
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "E400", "입력값이 올바르지 않습니다."),

	// 401 Unauthorized
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "J001", "토큰이 유효하지 않습니다."),
	UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "E401", "사용자 인증에 실패했습니다."),
	SJU_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "A001", "세종대 포털 인증에 실패했습니다."),

	// 403 Forbidden
	FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "E403", "접근 권한이 없습니다."),

	// 404 Not Found
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "사용자가 존재하지 않습니다."),
	PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "프로필이 존재하지 않습니다."),
	TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "팀이 존재하지 않습니다."),
	SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "스크랩 정보가 존재하지 않습니다."),

	// 409 Conflict
	MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "M002", "사용자가 이미 존재합니다."),

	// 502 Bad Gateway (Upstream Error)
	SJU_UPSTREAM_ERROR(HttpStatus.BAD_GATEWAY, "U001", "세종대 포털 연결에 실패했습니다."),

	// 500 Internal Server Error
	SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "서버 작업 중 예상치 못한 오류가 발생했습니다."),
	REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "D001", "레디스 작업 중 예상치 못한 오류가 발생했습니다.")
	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
