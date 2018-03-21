package me.grudzien.patryk.domain.entities.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ROLE")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "roleGenerator", sequenceName = "roleSequence", allocationSize = 1)
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleGenerator")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "ROLE_NAME", nullable = false)
	private String name;

	public Role(final String name) {
		this.name = name;
	}
}
