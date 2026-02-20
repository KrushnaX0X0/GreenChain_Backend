package com.krish.AgariBackend.dto.order;

//package com.krish.AgariBackend.dto;

public class OrderItemResponseDto {

	private String productName;
	private Integer quantity;
	private Double price;
	private Double totalPrice;

	// ✅ THIS CONSTRUCTOR MUST EXIST
	public OrderItemResponseDto() {
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	// getters

}
