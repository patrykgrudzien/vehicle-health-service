package me.grudzien.patryk.oauth2.authentication.chain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.NONE)
final class AuthenticationResult {

	private static class InstanceHolder {
		private static final AuthenticationResult INSTANCE = new AuthenticationResult();
	}

	static AuthenticationResult getInstance() {
		return InstanceHolder.INSTANCE;
	}

	enum Status {
		OK, FAILED
	}
}
