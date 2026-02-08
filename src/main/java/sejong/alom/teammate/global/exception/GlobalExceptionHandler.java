package sejong.alom.teammate.global.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.global.exception.docs.ErrorCode;
import sejong.alom.teammate.global.util.BaseResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BusinessException.class)
	protected ResponseEntity<BaseResponse<?>> handleBusinessException(BusinessException e) {
		ErrorCode errorCode = e.getErrorCode();

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(BaseResponse.fail(errorCode, e.getDetail()));
	}

	// 필수 파라미터 누락
	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<BaseResponse<?>> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
		ErrorCode errorCode = ErrorCode.INVALID_INPUT;

		log.warn("MissingServletRequestParameterException: param={}", e.getParameterName());
		Map<String, String> detail = Map.of(
			"code", errorCode.getCode(),
			"detail", e.getParameterName() + "is missing"
		);
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(BaseResponse.fail(errorCode, detail));
	}

	// Forbidden
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<BaseResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
		ErrorCode errorCode = ErrorCode.FORBIDDEN_ERROR;

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(BaseResponse.fail(errorCode));
	}

	// 서버 에러
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<BaseResponse<?>> handleAllExceptions(Exception e) {
		ErrorCode errorCode = ErrorCode.SERVER_ERROR;

		log.error("Unhandled Internal Server Error occurred:", e);

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(BaseResponse.fail(errorCode));
	}

	/*
	TODO: 추가할 에러
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        InvalidFormatException.class
    })

	 */
}
