package me.grudzien.patryk.registration.service.event;

import static com.google.common.base.Preconditions.checkNotNull;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.atomic.AtomicReference;

import me.grudzien.patryk.registration.model.dto.RegistrationResponse;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.event.RegistrationCompleteEvent;
import me.grudzien.patryk.utils.i18n.LocaleMessagesCreator;

@Slf4j
@Component
public class RegistrationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final LocaleMessagesCreator localeMessagesCreator;

    @Autowired
    public RegistrationEventPublisher(final ApplicationEventPublisher eventPublisher,
                                      final LocaleMessagesCreator localeMessagesCreator) {
        checkNotNull(eventPublisher, "eventPublisher cannot be null!");
        checkNotNull(localeMessagesCreator, "localeMessagesCreator cannot be null!");

        this.eventPublisher = eventPublisher;
        this.localeMessagesCreator = localeMessagesCreator;
    }

    public RegistrationResponse publishRegistrationEven(final CustomUser customUser, final WebRequest webRequest) {
        /**
         * I'm using Spring Event to create the token and send verification email (it should not be performed by controller directly).
         * One additional thing to notice is the Try<Void> wrapper surrounding the publishing of the event. This piece of code will set appropriate
         * {@link RegistrationResponse} based on either "success" or "failed" scenario after publishing
         * of the event, which in this case is the sending of the email.
         */
        final AtomicReference<RegistrationResponse> atomicResponse = new AtomicReference<>(new RegistrationResponse());
        Try.run(() -> {
            log.info("Publisher published event for verification token generation.");
            eventPublisher.publishEvent(new RegistrationCompleteEvent(customUser, webRequest.getContextPath()));
        }).onSuccess(successVoid -> {
            RegistrationResponse response = atomicResponse.get();
            response.setSuccessful(true);
            response.setMessage(localeMessagesCreator.buildLocaleMessageWithParam("register-user-account-success", customUser.getEmail()));
        }).onFailure(throwable -> {
        	// TODO: throw some meaningful exception in case of failure
            throw new RuntimeException(throwable.getMessage());
        });
        return atomicResponse.get();
    }
}
