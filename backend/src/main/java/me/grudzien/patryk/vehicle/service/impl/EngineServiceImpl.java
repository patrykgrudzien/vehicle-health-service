package me.grudzien.patryk.vehicle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.grudzien.patryk.vehicle.repository.EngineRepository;
import me.grudzien.patryk.vehicle.service.EngineService;

@Service
public class EngineServiceImpl implements EngineService {

	private final EngineRepository engineRepository;

	@Autowired
	public EngineServiceImpl(final EngineRepository engineRepository) {
		this.engineRepository = engineRepository;
	}
}
