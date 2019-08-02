package me.grudzien.patryk.utils.common;

import lombok.NoArgsConstructor;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
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

    public static Predicate<Object> isCollectionEmpty() {
        return ObjectUtils::isEmpty;
    }

    public static Predicate<Object> isCollectionNotEmpty() {
        return list -> !ObjectUtils.isEmpty(list);
    }

    public static boolean isNullOrEmpty(final @Nullable Object str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(final @Nullable Object str) {
        return !isNullOrEmpty(str);
    }

    public static boolean isNotNumeric(final CharSequence cs) {
        return !isNumeric(cs);
    }

    public static boolean isNotAlpha(final CharSequence cs) {
        return !isAlpha(cs);
    }

    private static boolean isAlpha(final CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isAlpha(cs);
    }

    private static boolean isNumeric(final CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isNumeric(cs);
    }
}
