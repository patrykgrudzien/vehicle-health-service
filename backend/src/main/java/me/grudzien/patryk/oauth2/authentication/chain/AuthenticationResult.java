package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.NONE)
public final class AuthenticationResult {

	private enum Status {
		OK, FAILED
	}

	private Status status;
	private Throwable throwable;
	private String throwableMessage;

	private static class InstanceHolder {
		private static final AuthenticationResult INSTANCE = new AuthenticationResult();
	}

	static AuthenticationResult getInstance() {
		return InstanceHolder.INSTANCE;
	}

	AuthenticationResult ok() {
		return getInstance().setStatus(Status.OK);
    }

    AuthenticationResult failed(final Throwable throwable, final String throwableMessage) {
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
}
