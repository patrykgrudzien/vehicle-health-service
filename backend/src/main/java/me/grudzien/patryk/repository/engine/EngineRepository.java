package me.grudzien.patryk.repository.engine;

import org.springframework.data.repository.CrudRepository;

import me.grudzien.patryk.domain.entity.engine.Engine;

public interface EngineRepository extends CrudRepository<Engine, Long> {}
