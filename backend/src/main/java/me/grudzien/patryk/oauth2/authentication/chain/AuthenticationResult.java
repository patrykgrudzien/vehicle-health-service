package me.grudzien.patryk.oauth2.authentication.chain;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.NONE)
public final class AuthenticationResult {

    private Map<Status, Tuple2<?, ?>> resultMap;

	private static class InstanceHolder {
		private static final AuthenticationResult INSTANCE = new AuthenticationResult();
	}

	static AuthenticationResult getInstance() {
		return InstanceHolder.INSTANCE;
	}

	AuthenticationResult OK() {
        final AuthenticationResult instance = getInstance();
        instance.setResultMap(HashMap.of(Status.OK, Tuple.of("No exception thrown.", "All authentication steps have been successfully passed.")));
        return instance;
    }

    AuthenticationResult FAILED(final Throwable throwable, final String throwableMessage) {
        final AuthenticationResult instance = getInstance();
        instance.setResultMap(HashMap.of(Status.FAILED, Tuple.of(throwable, throwableMessage)));
        return instance;
    }

    private enum Status {
        OK, FAILED
    }
}
