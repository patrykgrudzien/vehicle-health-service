package me.grudzien.patryk.oauth2.authentication.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationStepOrder {

    FIRST(1, "Get JWT From Authentication"),
    SECOND(2, "Get Key Id Attribute From JWT"),
    THIRD(3, "Verify Signature Using RSA"),
    FOURTH(4, "Decode JWT"),
    FIFTH(5, "Read Map Of Attributes From JWT"),
    SIXTH(6, "Load Email Attribute"),
    SEVENTH(7, "Load Subject Identifier"),
    EIGHT(8, "Clear Principal User Cache"),
    NINTH(9, "Load User From DB"),
    TENTH(10, "Pre Authentication Checks"),
    ELEVENTH(11, "Post Authentication Checks"),
    TWELFTH(12, "Additional Authentication Checks");

    private final int id;
    private final String description;
}
