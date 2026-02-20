package com.krish.AgariBackend.dto;

public class ProductResponseDto {

    public Long id;
    public String name;
    public Double price;
    public String unit;
    public String imageUrl;
    public String category;
    public Integer stock;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String name, Double price,
            String unit, String imageUrl, String category, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stock = stock;
    }
}
