package me.grudzien.patryk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.grudzien.patryk.domain.entities.registration.CustomUser;

public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {

	CustomUser findByEmail(String email);
}
