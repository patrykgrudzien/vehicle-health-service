package me.grudzien.patryk.utils.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

import static me.grudzien.patryk.utils.validation.ValidationGroups.*;

/**
 * Defines an order in which validations should be executed.
 */
@GroupSequence({Default.class, Step2.class, Step3.class})
interface ValidationSequence {}
