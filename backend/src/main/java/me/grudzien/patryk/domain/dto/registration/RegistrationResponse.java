package me.grudzien.patryk.domain.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RegistrationResponse implements Serializable {

    private static final long serialVersionUID = 4445835524695711394L;

    private String message;

    @JsonProperty("successful")
    private boolean isSuccessful;
}
