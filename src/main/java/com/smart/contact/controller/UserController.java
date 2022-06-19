package com.smart.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
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
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder; 
	
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("User Name for CommonData Method:- " + userName);

		User user = userRepository.getUserByUserName(userName);

		System.out.println("User Details for CommonData Method:- " + user);

		model.addAttribute("user", user);

	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
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
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile multipartFile, Principal principal, HttpSession session) {

		try {

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// for set user id in contact table

			contact.setUser(user);

			if (multipartFile.isEmpty()) {

				// if the file is empty then try our message

				System.out.println("File is Empty");
				contact.setImage("contact.png");

			} else {

				// upload the file into the folder
				contact.setImage(multipartFile.getOriginalFilename());

				File file = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());

				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");
			}

			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("Data:- " + contact);

			System.out.println("Added to database");

			// success message

			session.setAttribute("message", new Message("Your contact is added", "success"));

		} catch (Exception e) {
			System.out.println("ERROR:- " + e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong !! Try again...", "danger"));
		}
		return "normal/add_contact_form";
	}

	// view or list of contacts
	// per page = 5[n]
	// current page = 0 [page]

	@GetMapping("/view-contacts/{page}")
	public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "View Contact Page");

		// send list of contacts on html page

		/*
		 * String userName = principal.getName(); User user =
		 * this.userRepository.getUserByUserName(userName); List<Contact> contacts =
		 * user.getContacts();
		 */
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		Pageable p = PageRequest.of(page, 8);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), p);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/view_contacts";
	}

	// showing specific contact detail
	@RequestMapping("/contact/{cid}")
	public String showContatcDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		System.out.println("CID:- " + cid);

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		
		System.out.println("Specific Contact Details:- " +contactOptional);
		
		Contact contact = contactOptional.get();
		
		System.out.println("Specific Contact:- " +contact);

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		System.out.println("Specific Contact Method:- " +user);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}

	// delete a contact

	@GetMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();

		// check id

		System.out.println("Contact:- " + contact.getCid());

		//contact.setUser(null);
		
		//remove
		//img
		//contact.getImage();

		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		
		this.userRepository.save(user);

		System.out.println("Deleted");

		session.setAttribute("message", new Message("Contact deleted successfully...", "success"));

		return "redirect:/user/view-contacts/0";
	}
	
	// show update form handler
	@PostMapping("/update-form/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		
		model.addAttribute("title", "Update Contact");
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		model.addAttribute("contact", contact);
		
		
		return "normal/update_form";
	}
	
	// update contact handler
	
	@PostMapping("/process-update")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile multipartFile, Model model, HttpSession session, Principal principal) {
		
		try {
			
			// fetch old contact details
			Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
			
			//check new image
			if(!multipartFile.isEmpty()) {
				
				//file work
				// rewrite file
				
				//delete old photo
				

				File deleteFile = new ClassPathResource("static/image").getFile();
				File newFile = new File(deleteFile, oldContact.getImage());
				newFile.delete();

				
				
				// update new photo
				
				File file = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());

				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(multipartFile.getOriginalFilename());
				
			}else {
				
				contact.setImage(oldContact.getImage());
			}
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Contact Name:- " +contact.getName());
		System.out.println("Contact Id:- " +contact.getCid());
		
		return "redirect:/user/view-contacts/0";
	}
	
	// User profile handler
	@GetMapping("/getUser/profile")
	public String userProfile(Model model, Principal principal) {
		
		model.addAttribute("title", "User Profile Page");
		
		String userName = principal.getName();
		
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		
		System.out.println("User Profile Details:- " +user);
		
		return "normal/user_profile";
	}
	
	// open settings hanler or page
	@GetMapping("/settings")
	public String openSettings() {
		
		return "normal/settings";
	}
	
	// change password handler
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session, Model model) {
		
		model.addAttribute("title", "Setting Page");
		
		System.out.println("OLD PASSWORD: " +oldPassword);
		System.out.println("NEW PASSWORD: " +newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		
		System.out.println("CURRENT PASSWORD : " +currentUser.getPassword());
		//logger.debug("Mismatch old password {} with new password {} : " , oldPassword, newPassword);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			// change password
			
			
			
			
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			
			logger.info("Mismatch old password : {} with new password : {} " , oldPassword, newPassword);
			
			logger.info("New password has been change successfuly:  {} " , newPassword);
			session.setAttribute("message", new Message("Your password is successfully changed", "success"));
			
		}else {
			
			// error...
			session.setAttribute("message", new Message("Please enter correct old password !!!", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
	// creating order - payment gateway
	@PostMapping("/create/order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
		
		logger.info("Data {} : ", data);
		
		int amt = Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_MTN0sptGOn4ror", "FKGMv2t5q2P1m01u7QePvTsf");
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("amount", amt*100);
		jsonObject.put("currency", "INR");
		jsonObject.put("receipt", "txn_45423");
		
		// creating new order
		logger.info("Calling razopay api to create the order :");
		Order order = razorpayClient.Orders.create(jsonObject);
		logger.info("Razorpay response {} :", order);
		
		// save the order data in database
		
		
		return order.toString();
	}

}
