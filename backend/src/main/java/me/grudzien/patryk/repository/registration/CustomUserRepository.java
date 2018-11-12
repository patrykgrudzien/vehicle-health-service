package me.grudzien.patryk.repository.registration;

import org.springframework.data.repository.CrudRepository;

import me.grudzien.patryk.domain.entity.registration.CustomUser;

public interface CustomUserRepository extends CrudRepository<CustomUser, Long> {

	CustomUser findByEmail(String email);
}
