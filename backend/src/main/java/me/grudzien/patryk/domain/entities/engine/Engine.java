package me.grudzien.patryk.domain.entities.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import me.grudzien.patryk.domain.enums.engine.EngineType;

@Entity
@Table(name = "ENGINE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "engineGenerator", sequenceName = "engineSequence", allocationSize = 1)
public class Engine {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "engineGenerator")
	@Column(name = "ID")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "ENGINE_TYPE")
	private EngineType engineType;
}
