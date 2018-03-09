package me.grudzien.patryk.service;

import me.grudzien.patryk.domain.dto.EmailDto;

public interface EmailService {

	void sendSimpleMessage(EmailDto emailDto);

	void sendMessageUsingTemplate(EmailDto emailDto);
}
