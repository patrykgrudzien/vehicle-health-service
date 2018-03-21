package me.grudzien.patryk.repository.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.entities.registration.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	EmailVerificationToken findByToken(String token);

	EmailVerificationToken findByCustomUser(CustomUser customUser);
}
