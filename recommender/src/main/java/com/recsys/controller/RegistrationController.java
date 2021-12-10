package com.recsys.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.recsys.model.Account;
import com.recsys.service.AccountService;

@Controller
@RequestMapping("/register")
public class RegistrationController {

	@Autowired
	private AccountService accountService;
	
	@GetMapping
	public String registerForm(final Model model) {
		model.addAttribute("account", new Account());
		return "registration";
	}
	
	@PostMapping
	public String registerUser(final @Valid Account account, final BindingResult result,final Model model) {
		if(result.hasErrors()) {
			model.addAttribute("registrationForm",account);
			return "registration";
		}
		try {
			accountService.register(account);
		} catch(Exception e) {
			result.rejectValue("email", "account.email","A user with this email already exists");
			model.addAttribute("registrationForm",account);
			return "registration";
		}
		return "registration";
	}
	
}
