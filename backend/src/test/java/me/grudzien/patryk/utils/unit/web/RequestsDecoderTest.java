package me.grudzien.patryk.utils.unit.web;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Base64;
import java.util.stream.Stream;

import me.grudzien.patryk.utils.web.RequestsDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RequestsDecoderTest {

    private final RequestsDecoder requestsDecoder = new RequestsDecoder();

    private static Stream<Arguments> correctInputParams() {
        return Stream.of(
                arguments("Patryk"),
                arguments("Grudzień"),
                arguments("ąźćżóęść"),
                arguments("John"),
                arguments("Snow"),
                arguments("test@email.com"),
                arguments("password"),
                arguments("test."),
                arguments(".test"),
                arguments("_test"),
                arguments("test_")
        );
    }

    @ParameterizedTest(name = "Testing request decoder against: ({0}) correct input param.")
    @MethodSource("correctInputParams")
    void testRequestDecoder_correctInputParam(final String inputParam) {
        // given
        final String encodedInput = Base64.getEncoder().encodeToString(inputParam.getBytes());

        // when
        final String decodedInput = requestsDecoder.decodeStringParam(encodedInput);

        // then
        assertAll(
                () -> assertFalse(requestsDecoder.isParamEncoded(inputParam)),
                () -> assertTrue(requestsDecoder.isParamEncoded(encodedInput)),
                () -> assertFalse(requestsDecoder.isParamEncoded(decodedInput)),
                () -> assertThat(decodedInput).isEqualTo(inputParam)
        );
    }

	private static Stream<Arguments> incorrectInputParams() {
		return Stream.of(
				arguments(""),
				arguments((Object) null)
		);
	}

	@ParameterizedTest(name = "Testing request decoder against: ({0}) incorrect input param.")
	@MethodSource("incorrectInputParams")
	void testRequestDecoder_incorrectInputParam(final String inputParam) {
		// then
		assertAll(
				() -> assertFalse(requestsDecoder.isParamEncoded(inputParam)),
				() -> assertThat(requestsDecoder.decodeStringParam(inputParam)).isEqualTo(inputParam)
		);
	}
}
