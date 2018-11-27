package me.grudzien.patryk.integration.registration;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Disabled;

import me.grudzien.patryk.controller.registration.UserRegistrationController;

@Disabled
@SpringBootTest(classes = UserRegistrationController.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRegistrationControllerIT {}
