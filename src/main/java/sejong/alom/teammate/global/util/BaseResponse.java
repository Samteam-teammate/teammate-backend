package sejong.alom.teammate.global.util;

import org.springframework.http.HttpStatus;

public record BaseResponse<T>(
	HttpStatus status,
	String message,
	T detail
) {
	public static BaseResponse<?> success(String message) {
		return new BaseResponse<>(HttpStatus.OK, message, null);
	}
}
