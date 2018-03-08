package me.grudzien.patryk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.grudzien.patryk.domain.entities.CustomUser;
import me.grudzien.patryk.domain.entities.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

	EmailVerificationToken findByToken(String token);

	EmailVerificationToken findByCustomUser(CustomUser customUser);
}
