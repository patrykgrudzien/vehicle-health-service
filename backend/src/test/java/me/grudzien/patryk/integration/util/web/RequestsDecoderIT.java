package me.grudzien.patryk.integration.util.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Base64;
import java.util.stream.Stream;

import me.grudzien.patryk.util.web.RequestsDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestsDecoderIT {

    private final RequestsDecoder requestsDecoder = new RequestsDecoder();

    private static Stream<Arguments> inputParams() {
        return Stream.of(
                Arguments.arguments("Patryk"),
                Arguments.arguments("Grudzień"),
                Arguments.arguments("ąźćżóęść"),
                Arguments.arguments("John"),
                Arguments.arguments("Snow"),
                Arguments.arguments("test@email.com"),
                Arguments.arguments("password"),
                Arguments.arguments("test."),
                Arguments.arguments(".test"),
                Arguments.arguments("_test"),
                Arguments.arguments("test_")
        );
    }

    @ParameterizedTest(name = "Testing request decoder against: ({0}) input param.")
    @MethodSource("inputParams")
    void testRequestDecoder(final String inputParam) {
        // given
        final String encodedInput = Base64.getEncoder().encodeToString(inputParam.getBytes());

        // when
        final String decodedInput = requestsDecoder.decodeStringParam(encodedInput);

        // then
        Assertions.assertAll(
                () -> assertFalse(requestsDecoder.isParamEncoded(inputParam)),
                () -> assertTrue(requestsDecoder.isParamEncoded(encodedInput)),
                () -> assertThat(decodedInput).isEqualTo(inputParam)
        );
    }
}
