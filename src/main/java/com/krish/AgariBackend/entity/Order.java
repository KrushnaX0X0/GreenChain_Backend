package com.krish.AgariBackend.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 🔐 which user purchased
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	// 📦 product
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	private Integer quantity;

	@Column(name = "total_amount", nullable = false)
	private Double totalPrice;

	// 📍 delivery info
	private String mobile;
	private String address;
	private String city;

	private String status = "Pending"; // Default status
	private java.time.LocalDateTime orderDate = java.time.LocalDateTime.now(); // Default date

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public java.time.LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(java.time.LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

}
