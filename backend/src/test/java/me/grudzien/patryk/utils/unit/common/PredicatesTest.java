package me.grudzien.patryk.utils.unit.common;

import org.junit.jupiter.api.Test;

import me.grudzien.patryk.utils.common.Predicates;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class PredicatesTest {

    @Test
    void shouldReturnTrueIfObjectsAreNotNull() {
        // when
        final boolean allNonNull = Predicates.allNonNull(new Object(), new Object());

        // then
        assertThat(allNonNull).isTrue();
    }

    @Test
    void shouldReturnFalseWhenSomeOfTheObjectsAreNull() {
        // when
        final boolean allNonNull = Predicates.allNonNull(new Object(), null);

        // then
        assertThat(allNonNull).isFalse();
    }

    @Test
    void shouldReturnTrueIfObjectsAreNull() {
        // given
        final boolean allAreNull = Predicates.allAreNull(null, null);

        // then
        assertThat(allAreNull).isTrue();
    }

    @Test
    void shouldReturnFalseIfSomeOfTheObjectsAreNotNull() {
        // when
        final boolean allAreNull = Predicates.allAreNull(new Object(), null);

        // then
        assertThat(allAreNull).isFalse();
    }

    @Test
    void shouldReturnTrueWhenListContainsInstance() {
        // given
        final boolean containsInstance = Predicates.containsInstance(singletonList("test-element"), String.class);

        // then
        assertThat(containsInstance).isTrue();
    }

    @Test
    void shouldReturnFalseWhenListDoesNotContainInstance() {
        // when
        final boolean containsInstance = Predicates.containsInstance(singletonList(1234), String.class);

        // then
        assertThat(containsInstance).isFalse();
    }
}
