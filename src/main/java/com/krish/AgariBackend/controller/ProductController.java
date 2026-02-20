package com.krish.AgariBackend.controller;

import com.krish.AgariBackend.dto.ProductResponseDto;
import com.krish.AgariBackend.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
// Removed @CrossOrigin to avoid header duplication conflicts
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // CREATE PRODUCT (Restricted to ADMIN via SecurityConfig or @PreAuthorize)
    @PostMapping
    public ProductResponseDto create(@Validated @RequestBody ProductResponseDto dto) {
        return service.create(dto);
    }

    // GET ALL PRODUCTS (Public)
    @GetMapping
    public List<ProductResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // 🔐 ADMIN: Update Product
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ProductResponseDto update(@PathVariable Long id, @RequestBody ProductResponseDto dto) {
        return service.update(id, dto);
    }

    // 🔐 ADMIN: Delete Product
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}