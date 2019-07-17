package me.grudzien.patryk.registration.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

import me.grudzien.patryk.registration.model.enums.RoleName;

@Entity
@Table(name = "ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "roleGenerator", sequenceName = "roleSequence", allocationSize = 1)
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roleGenerator")
	@Column(name = "ID")
	private Long id;

	@Column(name = "ROLE_NAME", length = 50)
	@NotNull
	@Enumerated(EnumType.STRING)
	private RoleName roleName;

	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	private List<CustomUser> users;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "ROLES_PRIVILEGES",
			joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"),
			inverseJoinColumns = @JoinColumn(name = "PRIVILEGE_ID", referencedColumnName = "ID"))
	private Set<Privilege> privileges;

	public Role(@NotNull final RoleName roleName) {
		this.roleName = roleName;
	}
}
