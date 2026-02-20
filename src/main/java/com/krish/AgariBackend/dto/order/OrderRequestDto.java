package com.krish.AgariBackend.dto.order;


import java.util.List;

public class OrderRequestDto {

    private List<OrderItemRequestDto> items;
    private String mobile;
    private String address;
    private String city;

    public List<OrderItemRequestDto> getItems() { return items; }

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

	public void setItems(List<OrderItemRequestDto> items) {
		this.items = items;
	}
 
}
