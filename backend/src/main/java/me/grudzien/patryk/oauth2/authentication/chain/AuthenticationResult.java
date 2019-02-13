package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.grudzien.patryk.oauth2.authentication.CustomAuthenticationToken;

@Log4j2
@Getter
@NoArgsConstructor(access = AccessLevel.NONE)
public final class AuthenticationResult {

	public enum Status {
		OK, FAILED
    }

    private Status status;
    private Throwable throwable;
    private String throwableMessage;
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

    public AuthenticationResult failed(final Throwable throwable, final String throwableMessage) {
	    log.error("AuthenticationResult was FAILED!");
	    return getInstance().setStatus(Status.FAILED)
	                        .setThrowable(throwable)
	                        .setThrowableMessage(throwableMessage);
    }

	private AuthenticationResult setStatus(final Status status) {
		this.status = status;
		return this;
	}

	private AuthenticationResult setThrowable(final Throwable throwable) {
		this.throwable = throwable;
		return this;
	}

	private AuthenticationResult setThrowableMessage(final String throwableMessage) {
		this.throwableMessage = throwableMessage;
		return this;
	}

    private AuthenticationResult setCustomAuthenticationToken(final CustomAuthenticationToken customAuthenticationToken) {
	    this.customAuthenticationToken = customAuthenticationToken;
	    return this;
    }
}
