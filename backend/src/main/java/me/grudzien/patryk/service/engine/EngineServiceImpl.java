package me.grudzien.patryk.service.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.grudzien.patryk.repository.engine.EngineRepository;

@Service
public class EngineServiceImpl implements EngineService {

	private final EngineRepository engineRepository;

	@Autowired
	public EngineServiceImpl(final EngineRepository engineRepository) {
		this.engineRepository = engineRepository;
	}
}
