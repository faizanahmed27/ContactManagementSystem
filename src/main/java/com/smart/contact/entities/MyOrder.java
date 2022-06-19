package com.smart.contact.entities;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "MY_ORDER")
public class MyOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "ORDER_ID")
	private String orderId;
	
	@Column(name = "ORDER_AMOUNT")
	private int amount;
	
	@Column(name = "ORDER_RECEIPT")
	private String receipt;
	
	@Column(name = "ORDER_STATUS")
	private String status;
	
	@Column(name = "ORDER_PAYMENT_ID")
	private String paymentId;
	
	@ManyToOne
	@JoinColumn(name = "ORDER_USER_ID")
	private User user;

	public MyOrder(Long id, String orderId, int amount, String receipt, String status, String paymentId, User user) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.amount = amount;
		this.receipt = receipt;
		this.status = status;
		this.paymentId = paymentId;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "MyOrder [id=" + id + ", orderId=" + orderId + ", amount=" + amount + ", receipt=" + receipt
				+ ", status=" + status + ", paymentId=" + paymentId + ", user=" + user + "]";
	}
	
}
