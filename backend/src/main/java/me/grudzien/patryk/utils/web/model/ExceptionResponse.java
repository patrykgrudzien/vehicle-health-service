package me.grudzien.patryk.utils.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import me.grudzien.patryk.oauth2.model.entity.CustomOAuth2OidcPrincipalUser.AccountStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class ExceptionResponse extends CustomResponse {

	private List<String> errors;
	private String messageCode;

    public ExceptionResponse(final String message) {
        super(message);
    }

	@Builder(builderMethodName = "FullBuilder")
	public ExceptionResponse(final String message, final SecurityStatus securityStatus, final AccountStatus accountStatus, final String lastRequestedPath,
                             final String lastRequestMethod, final List<String> errors, final String messageCode) {
		super(message, securityStatus, accountStatus, lastRequestedPath, lastRequestMethod);
		this.errors = errors;
		this.messageCode = messageCode;
	}

	public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception) {
		return ExceptionResponse.FullBuilder()
		                        .message(exception.getMessage())
		                        .build();
	}

    public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception, final List<String> errors,
                                                                                  final String messageCode) {
        return ExceptionResponse.FullBuilder()
                                .message(exception.getMessage())
                                .errors(errors)
                                .messageCode(messageCode)
                                .build();
    }

	public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception, final SecurityStatus securityStatus) {
		return ExceptionResponse.FullBuilder()
		                        .message(exception.getMessage())
		                        .securityStatus(securityStatus)
		                        .build();
	}

    public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final AccountStatus accountStatus) {
        return ExceptionResponse.FullBuilder()
                                .accountStatus(accountStatus)
                                .build();
    }

    public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception, final AccountStatus accountStatus) {
        return ExceptionResponse.FullBuilder()
                                .message(exception.getMessage())
                                .accountStatus(accountStatus)
                                .build();
    }

	public static <T extends RuntimeException> ExceptionResponse buildBodyMessage(final T exception, final SecurityStatus securityStatus,
	                                                                              final String lastRequestedPath, final String lastRequestMethod) {
		return ExceptionResponse.FullBuilder()
		                        .message(exception.getMessage())
		                        .securityStatus(securityStatus)
		                        .lastRequestedPath(lastRequestedPath)
		                        .lastRequestMethod(lastRequestMethod)
		                        .build();
	}
}

