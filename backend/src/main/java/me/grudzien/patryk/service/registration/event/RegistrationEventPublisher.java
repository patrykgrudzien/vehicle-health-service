package me.grudzien.patryk.service.registration.event;

import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.google.common.base.Preconditions;

import java.util.concurrent.atomic.AtomicReference;

import me.grudzien.patryk.domain.dto.registration.RegistrationResponse;
import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.util.i18n.LocaleMessagesCreator;

@Log4j2
@Component
public class RegistrationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final LocaleMessagesCreator localeMessagesCreator;

    @Autowired
    public RegistrationEventPublisher(final ApplicationEventPublisher eventPublisher, final LocaleMessagesCreator localeMessagesCreator) {
        Preconditions.checkNotNull(eventPublisher, "eventPublisher cannot be null!");
        Preconditions.checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");
        this.eventPublisher = eventPublisher;
        this.localeMessagesCreator = localeMessagesCreator;
    }

    public RegistrationResponse publishRegistrationEven(final CustomUser customUser, final WebRequest webRequest) {
        final RegistrationResponse registrationResponse = RegistrationResponse.Builder(false).build();
        /**
         * I'm using Spring Event to create the token and send verification email (it should not be performed by controller directly).
         * One additional thing to notice is the Try<Void> wrapper surrounding the publishing of the event. This piece of code will set appropriate
         * {@link me.grudzien.patryk.domain.dto.registration.RegistrationResponse} based on either "success" or "failed" scenario after publishing
         * of the event, which in this case is the sending of the email.
         */
        final AtomicReference<RegistrationResponse> atomicResponse = new AtomicReference<>(registrationResponse);
        Try.run(() -> {
            log.info("Publisher published event for verification token generation.");
            eventPublisher.publishEvent(new RegistrationCompleteEvent(customUser, webRequest.getContextPath()));
        }).onSuccess(successVoid -> {
            RegistrationResponse response = atomicResponse.get();
            response.setSuccessful(true);
            response.setMessage(localeMessagesCreator.buildLocaleMessageWithParam("register-user-account-success", customUser.getEmail()));
        }).onFailure(throwable -> {
            throw new RuntimeException(throwable.getMessage());
        });
        return atomicResponse.get();
    }
}
