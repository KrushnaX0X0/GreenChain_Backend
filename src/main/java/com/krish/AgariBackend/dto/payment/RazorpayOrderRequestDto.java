package com.krish.AgariBackend.dto.payment;

public class RazorpayOrderRequestDto {
    private Long orderId;
    private Double amount; // in rupees
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
    
    
    
    
}

