package com.smart.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.contact.dao.ContactRepository;
import com.smart.contact.dao.UserRepository;
import com.smart.contact.entities.Contact;
import com.smart.contact.entities.User;
import com.smart.contact.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	
	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		
		String userName = principal.getName();
		System.out.println("User Name:- " +userName);
		
		User user = userRepository.getUserByUserName(userName);
		
		System.out.println("User Details:- " +user);
		
		model.addAttribute("user", user);
		
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		model.addAttribute("title", "User Dashboard");
		return  "normal/user_dashboard";
	}
	
	// open add contact form handler
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact Form");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	// processing add contact form
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile multipartFile , Principal principal, HttpSession session) {
		
		try {
			
		String name  = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		// for set user id in contact table
		
		contact.setUser(user);
		
		if(multipartFile.isEmpty()) {
			
			// if the file is empty then try our message
			
			System.out.println("File is Empty");
			
		}else {
			
			// upload the file into the folder
			contact.setImage(multipartFile.getOriginalFilename());
			
			File file = new ClassPathResource("static/image").getFile();
			
			Path path = Paths.get(file.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
			
			Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("Image is uploaded");
		}
		
		user.getContacts().add(contact);
		
		
		
		this.userRepository.save(user);
		
		System.out.println("Data:- " +contact);
		
		System.out.println("Added to database");
		
		// success message
		
		session.setAttribute("message", new Message("Your contact is added", "success"));
		
		}catch (Exception e) {
			System.out.println("ERROR:- " +e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong !! Try again...", "danger"));
		}
		return "normal/add_contact_form";
	}
	
	
	// view or list of contacts
	
	@GetMapping("/view-contacts")
	public String viewContacts(Model model, Principal principal) {
		
		model.addAttribute("title", "View Contact Page");
		
		// send list of contacts on html page
		
		/*
		 * String userName = principal.getName(); User user =
		 * this.userRepository.getUserByUserName(userName); List<Contact> contacts =
		 * user.getContacts();
		 */
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId());
		model.addAttribute("contacts", contacts);
		
		
		
		return "normal/view_contacts";
	}
	
		
}
