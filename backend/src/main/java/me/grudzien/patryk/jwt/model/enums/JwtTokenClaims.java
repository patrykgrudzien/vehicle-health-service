package me.grudzien.patryk.jwt.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtTokenClaims {

	USER_ROLES("USER_ROLES",
               "Custom claim that stores authorities of JwtUser"),
	SUBJECT("SUB",
	        "The \"sub\" (subject) claim identifies the principal that is the subject of the JWT. "
	               + "The Claims in a JWT are normally statements about the subject. The subject value MAY be scoped to be locally unique in "
	               + "the context of the issuer or MAY be globally unique. The processing of this claim is generally application specific. "
	               + "Use of this claim is OPTIONAL."),
	AUDIENCE("AUD",
	         "The \"aud\" (audience) claim identifies the recipients that the JWT is intended for. "
	         + "Each principal intended to process the JWT MUST identify itself with a value in the audience claim. "
	         + "If the principal processing the claim does not identify itself with a value in the \"aud\" claim when this claim "
	         + "is present, then the JWT MUST be rejected."),
	ISSUED_AT("IAT",
	          "The \"iat\" (issued at) claim identifies the time at which the JWT was issued. "
	          + "This claim can be used to determine the age of the JWT. Its value MUST be a number containing an IntDate value. "
	          + "Use of this claim is OPTIONAL.");

	private final String key;
	private final String description;
}
