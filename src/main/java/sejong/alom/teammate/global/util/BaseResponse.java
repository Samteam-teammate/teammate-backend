package sejong.alom.teammate.global.util;

import org.springframework.http.HttpStatus;

import sejong.alom.teammate.global.exception.docs.ErrorCode;

public record BaseResponse<T>(
	int status,
	String message,
	T detail
) {
	public static BaseResponse<?> success(String message) {
		return new BaseResponse<>(HttpStatus.OK.value(), message, null);
	}

	public static <T> BaseResponse<T> success(String message, T data) {
		return new BaseResponse<>(HttpStatus.OK.value(), message, data);
	}

	public static BaseResponse<?> fail(ErrorCode e) {
		return new BaseResponse<>(e.getStatus().value(), e.getMessage(), e.getCode());
	}

	public static <T> BaseResponse<T> fail(ErrorCode e, T detail) {
		return new BaseResponse<>(e.getStatus().value(), e.getMessage(), detail);
	}
}
