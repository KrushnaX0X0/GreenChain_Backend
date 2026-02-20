package com.krish.AgariBackend.dto.order;

public class OrderItemRequestDto {
    private Long productId;
    private Integer quantity;

    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
}
