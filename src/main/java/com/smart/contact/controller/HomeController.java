package com.smart.contact.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contact.dao.UserRepository;
import com.smart.contact.entities.User;
import com.smart.contact.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder PasswordEncoder;

	@GetMapping("/")
	public String home(Model model) {

		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {

		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {

		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// This is handler for register

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {

				System.out.println("You have not agreed terms and conditions !!");
				throw new Exception("You have not agreed terms and conditions !!");

			}
			
			if(bindingResult.hasErrors()) {
				
				System.out.println("ERROR:- " +bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImageURL("default.png");
			user.setPassword(PasswordEncoder.encode(user.getPassword()));
			System.out.println(PasswordEncoder);
			

			System.out.println("Agreement " + agreement);
			System.out.println("USER " + user);

			User result = this.userRepository.save(user);
			System.out.println("User:- " + result);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}
	
	// handler for custom login
	@GetMapping("/signin")
	public String customLogin() {
		
		return "login";
	}
}
