package com.smart.contact.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.contact.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	// Pagination
	// c.user.id user coming from contact class and id from user class
	
	@Query("from Contact as c where c.user.id =:userId")
	public List<Contact> findContactsByUser(@Param("userId") int userId);

}
