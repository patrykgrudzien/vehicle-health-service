package me.grudzien.patryk.integration.registration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import me.grudzien.patryk.controller.registration.UserRegistrationController;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserRegistrationController.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRegistrationControllerIntegrationTest {}
