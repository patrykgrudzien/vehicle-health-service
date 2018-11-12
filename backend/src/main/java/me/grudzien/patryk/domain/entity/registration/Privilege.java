package me.grudzien.patryk.domain.entity.registration;

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
import javax.validation.constraints.NotNull;

import me.grudzien.patryk.domain.enums.registration.PrivilegeName;

@Entity
@Table(name = "PRIVILEGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "privilegeGenerator", sequenceName = "privilegeGenerator", allocationSize = 1)
public class Privilege {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "privilegeGenerator")
	@Column(name = "ID")
	private Long id;

	@Column(name = "PRIVILEGE_NAME", length = 50)
	@NotNull
	@Enumerated(EnumType.STRING)
	private PrivilegeName privilegeName;

	public Privilege(@NotNull final PrivilegeName privilegeName) {
		this.privilegeName = privilegeName;
	}
}
