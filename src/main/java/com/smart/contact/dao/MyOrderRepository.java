package com.smart.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.contact.entities.MyOrder;

@Repository
public interface MyOrderRepository extends JpaRepository<MyOrder, Long> {

	public MyOrder findByOrderId(String orderId);
}
