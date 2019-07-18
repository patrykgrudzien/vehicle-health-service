package me.grudzien.patryk.utils.web;

import lombok.NoArgsConstructor;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Stream;

import static lombok.AccessLevel.NONE;

@NoArgsConstructor(access = NONE)
public final class MvcPattern {

    public static String of(final String... paths) {
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        Stream.of(paths).forEach(uriBuilder::path);
        return uriBuilder.toUriString();
    }
}
