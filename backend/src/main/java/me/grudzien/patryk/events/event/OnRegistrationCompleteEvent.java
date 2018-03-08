package me.grudzien.patryk.events.event;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.ApplicationEvent;

import me.grudzien.patryk.domain.entities.CustomUser;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4183581670384034845L;

	private String applicationUrl;
	private CustomUser customUser;
	private String eventName;

	public OnRegistrationCompleteEvent(final CustomUser customUser, final String applicationUrl) {
		// customUser in super() is the object on which the event initially occurred.
		super(customUser);
		this.customUser = customUser;
		this.applicationUrl = applicationUrl;
		this.eventName = this.getClass().getSimpleName();
	}
}
