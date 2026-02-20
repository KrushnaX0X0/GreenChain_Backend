package com.krish.AgariBackend.dto.order;

import java.util.List;

public class OrderResponseDto {

    private Long orderId;
    private Double totalAmount;
    private String city;
    private String customerName;
    private String mobile;
    private String address;
    private String status;
    private String orderDate;
    private List<OrderItemResponseDto> items;

    public OrderResponseDto(
            Long orderId,
            Double totalAmount,
            String city,
            String customerName,
            String mobile,
            String address,
            String status,
            String orderDate,
            List<OrderItemResponseDto> items) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.city = city;
        this.customerName = customerName;
        this.mobile = mobile;
        this.address = address;
        this.status = status;
        this.orderDate = orderDate;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getCity() {
        return city;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public List<OrderItemResponseDto> getItems() {
        return items;
    }
}
