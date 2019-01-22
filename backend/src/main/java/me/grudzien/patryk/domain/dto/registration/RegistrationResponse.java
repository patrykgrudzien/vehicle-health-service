package me.grudzien.patryk.domain.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import me.grudzien.patryk.domain.entity.registration.CustomUser;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "hiddenBuilder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RegistrationResponse implements Serializable {

    private static final long serialVersionUID = 4183765640561510178L;

    private String message;

    @JsonProperty("successful")
    private boolean isSuccessful;

    private String redirectionUrl;

    private CustomUser registeredUser;

    public static RegistrationResponseBuilder Builder(final boolean isSuccessful) {
        return hiddenBuilder().isSuccessful(isSuccessful);
    }
}
