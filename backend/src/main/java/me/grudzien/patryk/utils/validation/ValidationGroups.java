package me.grudzien.patryk.utils.validation;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.NONE;

/**
 * Contains interfaces that could be used for sequencing constraints validation.
 * {@link javax.validation.GroupSequence}.
 */
@NoArgsConstructor(access = NONE)
final class ValidationGroups {

    /**
     * Group of validations to be executed after {@link javax.validation.GroupSequence} ones.
     * @see me.grudzien.patryk.utils.validation.ValidationSequence
     */
    interface Step2 {}

    /**
     * Group of validations to be executed after {@link Step2} ones.
     * @see me.grudzien.patryk.utils.validation.ValidationSequence
     */
    interface Step3 {}
}
