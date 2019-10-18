package me.grudzien.patryk.registration.integration.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.apache.commons.lang3.builder.ToStringBuilder;

import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PROTECTED;
import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;

@NoArgsConstructor(access = NONE)
abstract class AbstractUserRegistrationServiceHelper {

    @Builder
    @Getter(PROTECTED)
    static class ConfirmRegistrationTestDataBuilder {
        private String language;
        private String registrationResponseMessage;
        private String exceptionMessage;

        @Override
        public String toString() {
            return new ToStringBuilder(this, NO_CLASS_NAME_STYLE)
                    .append("language", language)
                    .append("registrationResponseMessage", registrationResponseMessage)
                    .append("exceptionMessage", exceptionMessage)
                    .toString();
        }
    }
}
