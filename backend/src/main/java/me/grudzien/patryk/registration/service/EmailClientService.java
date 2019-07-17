package me.grudzien.patryk.registration.service;

import me.grudzien.patryk.registration.model.dto.EmailDto;

public interface EmailClientService {

	void sendSimpleMessage(EmailDto emailDto);

	void sendMessageUsingTemplate(EmailDto emailDto);
}
