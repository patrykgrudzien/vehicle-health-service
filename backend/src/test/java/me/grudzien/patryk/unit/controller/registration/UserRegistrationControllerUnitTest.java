package me.grudzien.patryk.unit.controller.registration;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import me.grudzien.patryk.controller.registration.UserRegistrationController;

@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserRegistrationController.class)
public class UserRegistrationControllerUnitTest {}
