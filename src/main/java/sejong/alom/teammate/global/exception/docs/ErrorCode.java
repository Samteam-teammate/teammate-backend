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
	R Recruitment, Apply
	C Calendar, Event
	*/

	// 400 Bad Request
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "E400", "입력값이 올바르지 않습니다."),
	INVALID_MEMBER_COUNT(HttpStatus.BAD_REQUEST, "T002", "최대 팀원 수를 초과할 수 없습니다."),
	INVALID_APPLY_PART(HttpStatus.BAD_REQUEST, "R002", "해당 공고에서 모집하지 않는 파트입니다."),

	// 401 Unauthorized
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "J001", "토큰이 유효하지 않습니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "J002", "만료된 토큰입니다."),
	UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "E401", "사용자 인증에 실패했습니다."),
	SJU_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "A001", "세종대 포털 인증에 실패했습니다."),

	// 403 Forbidden
	FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "E403", "접근 권한이 없습니다."),

	// 404 Not Found
	NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "정보가 존재하지 않습니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "사용자가 존재하지 않습니다."),
	PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "프로필이 존재하지 않습니다."),
	TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "팀이 존재하지 않습니다."),
	SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "스크랩 정보가 존재하지 않습니다."),
	RECRUITMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "모집 공고가 존재하지 않습니다."),
	EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "일정이 존재하지 않습니다."),
	APPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "지원 정보가 존재하지 않습니다."),

	// 409 Conflict
	MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "M002", "사용자가 이미 존재합니다."),
	ALREADY_SCRAPPED(HttpStatus.CONFLICT, "S002", "이미 스크랩 되었습니다."),
	ALREADY_TEAM_MEMBER(HttpStatus.CONFLICT, "T003", "이미 소속된 팀입니다."),
	ALREADY_APPLIED(HttpStatus.CONFLICT, "M003", "이미 지원한 공고입니다."),

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
