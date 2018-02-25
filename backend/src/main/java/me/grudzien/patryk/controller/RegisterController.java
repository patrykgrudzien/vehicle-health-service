package me.grudzien.patryk.controller;

import lombok.extern.log4j.Log4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.domain.dto.RegisterUser;

@RestController
@RequestMapping("/register")
@Log4j
public class RegisterController {

	@PostMapping("/check")
	public @ResponseBody String checkRegisterEndpoint(@RequestBody final RegisterUser registerUser) {
		log.info(">> name:" + registerUser.getName());
		log.info(">> lastName:" + registerUser.getLastName());
		log.info(">> email:" + registerUser.getEmail());
		log.info(">> password:" + registerUser.getPassword());
		return "checkRegisterEndpoint() called successfully!";
	}
}
