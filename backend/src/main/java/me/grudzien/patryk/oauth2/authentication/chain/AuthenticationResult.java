package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.grudzien.patryk.oauth2.authentication.model.CustomAuthenticationToken;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.NONE)
public final class AuthenticationResult {

	public enum Status {
		OK, FAILED
    }

    private Status status;
    private Class<? extends Throwable> exceptionClass;
    private String exceptionMessage;
    private CustomAuthenticationToken customAuthenticationToken;

	private static class InstanceHolder {
		private static final AuthenticationResult INSTANCE = new AuthenticationResult();
	}

	static AuthenticationResult getInstance() {
		return InstanceHolder.INSTANCE;
	}

	AuthenticationResult ok(final CustomAuthenticationToken customAuthenticationToken) {
	    log.info("AuthenticationResult was OK.");
		return getInstance().setStatus(Status.OK)
                            .setCustomAuthenticationToken(customAuthenticationToken);
    }

    public AuthenticationResult failed(final Class<? extends Throwable> exceptionClass, final String exceptionMessage) {
	    log.error("AuthenticationResult was FAILED!");
	    return getInstance().setStatus(Status.FAILED)
	                        .setExceptionClass(exceptionClass)
	                        .setExceptionMessage(exceptionMessage);
    }

	private AuthenticationResult setStatus(final Status status) {
		this.status = status;
		return this;
	}

	private AuthenticationResult setExceptionClass(final Class<? extends Throwable> exceptionClass) {
		this.exceptionClass = exceptionClass;
		return this;
	}

	private AuthenticationResult setExceptionMessage(final String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
		return this;
	}

    private AuthenticationResult setCustomAuthenticationToken(final CustomAuthenticationToken customAuthenticationToken) {
	    this.customAuthenticationToken = customAuthenticationToken;
	    return this;
    }
}
