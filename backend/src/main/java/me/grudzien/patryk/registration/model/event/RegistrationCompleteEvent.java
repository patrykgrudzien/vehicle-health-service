package me.grudzien.patryk.registration.model.event;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.ApplicationEvent;

import me.grudzien.patryk.registration.model.entity.CustomUser;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = 363547457177994767L;

	private String applicationUrl;
	private CustomUser customUser;
	private String eventName;

	public RegistrationCompleteEvent(final CustomUser customUser, final String applicationUrl) {
		// customUser in super() is the object on which the event initially occurred.
		super(customUser);
		this.customUser = customUser;
		this.applicationUrl = applicationUrl;
		this.eventName = this.getClass().getSimpleName();
	}
}
