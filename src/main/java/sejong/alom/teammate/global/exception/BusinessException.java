package sejong.alom.teammate.global.exception;

import lombok.Getter;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Getter
public class BusinessException extends RuntimeException{
	private final ErrorCode errorCode;
	private final Object detail;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.detail = null;
	}

	public BusinessException(ErrorCode errorCode, Object detail) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.detail = detail;
	}
}
