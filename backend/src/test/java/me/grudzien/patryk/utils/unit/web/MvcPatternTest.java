package me.grudzien.patryk.utils.unit.web;

import org.junit.jupiter.api.Test;

import me.grudzien.patryk.utils.web.MvcPattern;

import static org.assertj.core.api.Assertions.assertThat;

class MvcPatternTest {

    @Test
    void shouldCreateUriForTwoPaths() {
        // when
        final String uri = MvcPattern.of("/api", "/test-resource");

        // then
        assertThat(uri).isEqualTo("/api/test-resource");
    }
}
