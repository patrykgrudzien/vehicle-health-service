package me.grudzien.patryk.domain.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse extends CustomResponse {

	private String message;
	private List<String> errors;
	private String code;

	public static <T extends RuntimeException> ExceptionResponse buildMessage(final T exception) {
		return ExceptionResponse.Builder()
		                        .message(exception.getMessage())
		                        .build();
	}

	public static <T extends RuntimeException> ExceptionResponse buildMessageWithExceptionCode(final T exception,
	                                                                                           final CustomResponse.Codes code) {
		return ExceptionResponse.Builder()
		                        .message(exception.getMessage())
		                        .code(code.getCodeMessage())
		                        .build();
	}
}

