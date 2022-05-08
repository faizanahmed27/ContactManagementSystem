
package com.smart.contact.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contact.dao.UserRepository;
import com.smart.contact.entities.User;
import com.smart.contact.service.EmailService;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	Random random = new Random(1000);

	// email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {

		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {

		System.out.println("EMAIL :- " + email);

		// generating otp of 4 digits

		int otp = random.nextInt(999999);

		System.out.println("OTP :- " + otp);

		// code for send OTP to email..

		String subject = "OTP for changing password";
		
		String message = ""
				+ "<div style='border:1px solid #e2e2e2; padding:20px'> "
				+ "<h1>"
				+ "OTP is "
				+ "<b>" +otp
				+ "</n>"
				+ "</h1>"
				+ "</div>";
		
		String to = email;
		
		boolean sendOTP = this.emailService.sendEmail(subject, message, to);
		
		if(sendOTP) {
			
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else {
			
			session.setAttribute("message", "Please Check your an email id !!");
			
			return "forgot_email_form";
			
		}

	}
	
	// verify otp
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {
		
		int myotp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");
		
		if(myotp==otp) {
			
			User user = this.userRepository.getUserByUserName(email);
			
			if(user==null) {
				
				// send error
				
				session.setAttribute("message", "User does not exits with this an email !!");
				
				return "forgot_email_form";
				
			}else {
				
				// send change password
			}
			
			return "password_change_form";
		}else {
			
			session.setAttribute("message", "You have entered invalid OTP !!");
			return "verify_otp";
		}
		
		
	}
	
	// change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam ("newpassword") String newpassword, HttpSession session) {
		
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.passwordEncoder.encode(newpassword));
		
		this.userRepository.save(user);
		
		
		
		return "redirect:/signin?change=password changed successfully..";
	}
}
