package me.grudzien.patryk.domain.entity.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Date;
import java.util.List;
import java.util.Set;

import me.grudzien.patryk.domain.entity.vehicle.Vehicle;
import me.grudzien.patryk.domain.enums.registration.RegistrationProvider;

@Entity
@Table(name = "CUSTOM_USER", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@ToString(exclude = {"password", "lastPasswordResetDate", "roles", "vehicles"})
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "customUserGenerator", sequenceName = "customUserSequence", allocationSize = 1)
public class CustomUser {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customUserGenerator")
	@Column(name = "ID")
	private Long id;

	@Column(name = "FIRST_NAME", length = 50)
	@NotNull
	@Size(min = 4, max = 50)
	private String firstName;

	@Column(name = "LAST_NAME", length = 50)
	@NotNull
	@Size(min = 4, max = 50)
	private String lastName;

	@Column(name = "EMAIL", length = 50, unique = true)
	@NotNull
	@Size(min = 4, max = 50)
	private String email;

	@Column(name = "PASSWORD")
	@NotNull
	@Size(min = 4)
	private String password;

	@Column(name = "PROFILE_PICTURE_URL")
	private String profilePictureUrl;

	@Column(name = "ENABLED")
	@NotNull
	private boolean isEnabled;

	@Enumerated(EnumType.STRING)
	@Column(name = "REGISTRATION_PROVIDER")
	private RegistrationProvider registrationProvider;

	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date createdDate;

	@Column(name = "LAST_PASSWORD_RESET_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastPasswordResetDate;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "USERS_ROLES",
	           joinColumns = @JoinColumn(name = "CUSTOM_USER_ID", referencedColumnName = "ID"),
	           inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
	private Set<Role> roles;

	@OneToMany(mappedBy = "customUser", cascade = CascadeType.ALL)
	@JsonBackReference
	private List<Vehicle> vehicles;

	public CustomUser() {
		this.isEnabled = false;
		this.createdDate = new Date();
	}
}
