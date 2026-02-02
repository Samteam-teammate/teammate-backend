package sejong.alom.teammate.global.util;

import org.springframework.http.HttpStatus;

public record BaseResponse<T>(
	HttpStatus status,
	String message,
	T detail
) {
}
