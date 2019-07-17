package me.grudzien.patryk.vehicle.repository;

import org.springframework.data.repository.CrudRepository;

import me.grudzien.patryk.vehicle.model.entity.Engine;

public interface EngineRepository extends CrudRepository<Engine, Long> {}
