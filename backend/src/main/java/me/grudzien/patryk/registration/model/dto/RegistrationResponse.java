package me.grudzien.patryk.registration.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import me.grudzien.patryk.registration.model.entity.CustomUser;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder", builderClassName = "Builder")
@JsonInclude(NON_NULL)
public final class RegistrationResponse implements Serializable {

    private static final long serialVersionUID = 4183765640561510178L;

    private String message;

    @JsonProperty("successful")
    private boolean isSuccessful;

    private String redirectionUrl;

    private CustomUser registeredUser;
}
