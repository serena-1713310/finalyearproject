package com.recsys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * handles login page requests 
 */
@Controller
public class LoginController {

	/*
	 * GET request for login page
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/")
    public String index() {
        return "welcome";
    }
	
}