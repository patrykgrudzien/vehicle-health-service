package me.grudzien.patryk.registration.repository;

import org.springframework.data.repository.CrudRepository;

import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, Long> {

	EmailVerificationToken findByToken(String token);

	EmailVerificationToken findByCustomUser(CustomUser customUser);

	EmailVerificationToken findByCustomUserEmail(String customUserEmail);
}
