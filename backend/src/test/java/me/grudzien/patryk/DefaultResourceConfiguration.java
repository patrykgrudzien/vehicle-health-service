package me.grudzien.patryk;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

import me.grudzien.patryk.configuration.i18n.LocaleMessagesConfig;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;
import me.grudzien.patryk.utils.i18n.LocaleMessagesHelper;
import me.grudzien.patryk.utils.validation.ValidationService;

@TestConfiguration
@Import({
    ValidationService.class,
    LocaleMessagesHelper.class,
    LocaleMessagesCreator.class,
    LocaleMessagesConfig.class
})
public class DefaultResourceConfiguration {}
