package me.grudzien.patryk.utils.common;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
public final class Predicates {

    public static boolean allAreNull(final Object... objects) {
        return Stream.of(objects).allMatch(Objects::isNull);
    }

    public static boolean allNonNull(final Object... objects) {
        return Stream.of(objects).allMatch(Objects::nonNull);
    }

    public static <E> boolean containsInstance(final List<E> list, final Class<? extends E> clazz) {
        return list.stream().anyMatch(clazz::isInstance);
    }
}
