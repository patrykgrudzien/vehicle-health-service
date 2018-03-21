package me.grudzien.patryk.domain.entities.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Calendar;
import java.util.Date;

/**
 * The EmailVerificationToken must meet the following criteria:
 * 1) It must link back to the CustomUser (via a unidirectional relation)
 * 2) It'll be created right after registration
 * 3) It'll expire within 24h following its creation
 * 4) Has a unique, randomly generated value
 */
@Entity
@Table(name = "EMAIL_VERIFICATION_TOKEN")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken {

	private static final int EXPIRATION_IN_MINUTES = 24 * 60;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "TOKEN")
	private String token;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOM_USER_ID", nullable = false)
	private CustomUser customUser;

	@Column(name = "EXPIRY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;

	public EmailVerificationToken(final String token) {
		this.token = token;
		this.expiryDate = calculateExpiryDate(EXPIRATION_IN_MINUTES);
	}

	public EmailVerificationToken(final String token, final CustomUser customUser) {
		this.token = token;
		this.customUser = customUser;
		this.expiryDate = calculateExpiryDate(EXPIRATION_IN_MINUTES);
	}

	public void updateToken(final String newToken) {
		this.token = newToken;
		this.expiryDate = calculateExpiryDate(EXPIRATION_IN_MINUTES);
	}

	private Date calculateExpiryDate(final int expiryTimeInMinutes) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(new Date().getTime());
		calendar.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(calendar.getTime().getTime());
	}
}
