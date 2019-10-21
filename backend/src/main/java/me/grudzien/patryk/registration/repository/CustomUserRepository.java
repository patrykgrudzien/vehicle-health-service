package me.grudzien.patryk.registration.repository;

import org.springframework.data.repository.CrudRepository;

import me.grudzien.patryk.registration.model.entity.CustomUser;

public interface CustomUserRepository extends CrudRepository<CustomUser, Long> {

	CustomUser findByEmail(String email);

	boolean existsByEmail(String email);
}
