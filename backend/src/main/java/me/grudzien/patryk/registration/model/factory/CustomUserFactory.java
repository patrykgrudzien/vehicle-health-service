package me.grudzien.patryk.registration.model.factory;

import lombok.NoArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.ImmutableSet;

import me.grudzien.patryk.registration.model.dto.UserRegistrationDto;
import me.grudzien.patryk.registration.model.entity.CustomUser;
import me.grudzien.patryk.registration.model.entity.Privilege;
import me.grudzien.patryk.registration.model.entity.Role;
import me.grudzien.patryk.registration.model.enums.RegistrationProvider;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.NONE;

import static me.grudzien.patryk.registration.model.enums.PrivilegeName.CAN_DO_EVERYTHING;
import static me.grudzien.patryk.registration.model.enums.RegistrationProvider.CUSTOM;
import static me.grudzien.patryk.registration.model.enums.RoleName.ROLE_ADMIN;
import static me.grudzien.patryk.utils.appplication.ApplicationZone.POLAND;

@NoArgsConstructor(access = NONE)
public final class CustomUserFactory {

    public static CustomUser createFrom(final UserRegistrationDto registrationDto,
                                        final PasswordEncoder passwordEncoder) {
        final RegistrationProvider registrationProvider = registrationDto.getRegistrationProvider();
        return CustomUser.Builder()
                         .firstName(registrationDto.getFirstName())
                         .lastName(registrationDto.getLastName())
                         .email(registrationDto.getEmail())
                         .hasFakeEmail(registrationDto.isHasFakeEmail())
                         .password(passwordEncoder.encode(registrationDto.getPassword()))
                         .profilePictureUrl(registrationDto.getProfilePictureUrl())
                         .registrationProvider(isNull(registrationProvider) ? CUSTOM : registrationProvider)
                         .roles(ImmutableSet.of(Role.Builder()
                                                    .roleName(ROLE_ADMIN)
                                                    .privileges(newHashSet(Privilege.Builder().privilegeName(CAN_DO_EVERYTHING).build()))
                                                    .build()))
                         .createdDate(POLAND.now())
                         .build();
    }
}
