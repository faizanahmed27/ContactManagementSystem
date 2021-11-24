package com.smart.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.contact.entities.Contact;
import com.smart.contact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	// Pagination
	// c.user.id user coming from contact class and id from user class
	
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	
	// method for searching field
	
	public List<Contact> findByNameContainingAndUser(String name, User user);

}
