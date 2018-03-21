package me.grudzien.patryk.domain.entities.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.Collection;

@Entity
@Table(name = "CUSTOM_USER", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@ToString(of = {"id", "firstName", "lastName", "email", "roles", "isEnabled"})
@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "customUserGenerator", sequenceName = "customUserSequence", allocationSize = 1)
public class CustomUser {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customUserGenerator")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "FIRST_NAME", nullable = false)
	private String firstName;

	@Column(name = "LAST_NAME", nullable = false)
	private String lastName;

	@Column(name = "EMAIL", nullable = false)
	private String email;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "ENABLED")
	private boolean isEnabled;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "USERS_ROLES",
	           joinColumns = @JoinColumn(name = "CUSTOM_USER_ID", referencedColumnName = "ID"),
	           inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
	private Collection<Role> roles;

	public CustomUser() {
		this.isEnabled = false;
	}
}
