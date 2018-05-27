package me.grudzien.patryk.events.registration;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.context.request.WebRequest;

import me.grudzien.patryk.domain.entities.registration.CustomUser;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4183581670384034845L;

	private String applicationUrl;
	private CustomUser customUser;
	private String eventName;
	private WebRequest webRequest;

	public OnRegistrationCompleteEvent(final CustomUser customUser, final String applicationUrl, final WebRequest webRequest) {
		// customUser in super() is the object on which the event initially occurred.
		super(customUser);
		this.customUser = customUser;
		this.applicationUrl = applicationUrl;
		this.eventName = this.getClass().getSimpleName();
		// need "webRequest" to get "Language" header and determine Locale object
		this.webRequest = webRequest;
	}
}
