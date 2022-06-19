package com.smart.contact.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message, String to) {

		// rest of the code...

		boolean flag = false;

		String from = "fa45607@gmail.com";

		// variable for gmail

		String host = "smtp.gmail.com";

// get the system properties

		Properties properties = System.getProperties();
		System.out.println("PROPERTIES" + properties);

		// setting important information to properties object

		// set host

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1: to get the session object...

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication("fa45607@gmail.com", "Faizu786");
			}
			
		});

		// Step 2: compose the message [text, multi media]
		MimeMessage mimeMessage = new MimeMessage(session);

		// from email
		try {

			mimeMessage.setFrom(from);

			// adding recipient to message
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message;
			mimeMessage.setSubject(subject);

			// adding text to message
			//mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");

			// Step 3: send the message using Transport class
			Transport.send(mimeMessage);

			System.out.println("Message Sent Successfully...........");
			
			flag = true;

		} catch (MessagingException e) {

			e.printStackTrace();
		}
		
		return flag;
	}

}
