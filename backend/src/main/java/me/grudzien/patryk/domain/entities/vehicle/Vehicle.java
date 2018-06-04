package me.grudzien.patryk.domain.entities.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import me.grudzien.patryk.domain.entities.engine.Engine;
import me.grudzien.patryk.domain.entities.registration.CustomUser;
import me.grudzien.patryk.domain.enums.vehicle.VehicleType;

@Entity
@Table(name = "VEHICLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "vehicleGenerator", sequenceName = "vehicleSequence", allocationSize = 1)
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicleGenerator")
	@Column(name = "ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOM_USER_ID")
	@JsonBackReference
	private CustomUser customUser;

	@Enumerated(EnumType.STRING)
	@Column(name = "VEHICLE_TYPE")
	private VehicleType vehicleType;

	@OneToOne
	@JoinColumn(name = "ENGINE_ID")
	private Engine engine;
}
