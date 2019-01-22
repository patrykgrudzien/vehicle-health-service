package me.grudzien.patryk.repository.registration;

import org.springframework.data.repository.CrudRepository;

import me.grudzien.patryk.domain.entity.registration.CustomUser;
import me.grudzien.patryk.domain.entity.registration.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, Long> {

	EmailVerificationToken findByToken(String token);

	EmailVerificationToken findByCustomUser(CustomUser customUser);

	EmailVerificationToken findByCustomUser_Email(String customUserEmail);
}
