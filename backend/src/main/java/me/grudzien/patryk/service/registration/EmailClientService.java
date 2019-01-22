package me.grudzien.patryk.service.registration;

import me.grudzien.patryk.domain.dto.registration.EmailDto;

public interface EmailClientService {

	void sendSimpleMessage(EmailDto emailDto);

	void sendMessageUsingTemplate(EmailDto emailDto);
}
