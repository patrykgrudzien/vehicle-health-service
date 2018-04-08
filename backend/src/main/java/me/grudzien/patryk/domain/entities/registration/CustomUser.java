package me.grudzien.patryk.domain.entities.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "CUSTOM_USER", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@AllArgsConstructor
@Builder(builderMethodName = "Builder")
@SequenceGenerator(name = "customUserGenerator", sequenceName = "customUserSequence", allocationSize = 1)
public class CustomUser {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customUserGenerator")
	@Column(name = "ID")
	private Long id;

	@Column(name = "USERNAME", length = 100, unique = true)
	@NotNull
	@Size(min = 4, max = 100)
	private String username;

	@Column(name = "FIRST_NAME", length = 50)
	@NotNull
	@Size(min = 4, max = 50)
	private String firstName;

	@Column(name = "LAST_NAME", length = 50)
	@NotNull
	@Size(min = 4, max = 50)
	private String lastName;

	@Column(name = "EMAIL", length = 50)
	@NotNull
	@Size(min = 4, max = 50)
	private String email;

	@Column(name = "PASSWORD", length = 50)
	@NotNull
	@Size(min = 4, max = 50)
	private String password;

	@Column(name = "ENABLED")
	@NotNull
	private boolean isEnabled;

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
	private Collection<Role> roles;

	public CustomUser() {
		this.isEnabled = false;
		this.createdDate = new Date();
		this.username = this.firstName + this.lastName;
	}
}
