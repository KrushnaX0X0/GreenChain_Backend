package com.krish.AgariBackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

//import com.krish.AgariBackend.dto.ProductRequestDto;
import com.krish.AgariBackend.dto.ProductResponseDto;
import com.krish.AgariBackend.entity.Product;
import com.krish.AgariBackend.exception.ResourceNotFoundException;
import com.krish.AgariBackend.repo.ProductRepository;
//import com.krish.AgariBackend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public ProductResponseDto create(ProductResponseDto dto) {

        Product p = new Product();
        p.setName(dto.name);
        p.setPrice(dto.price);
        p.setUnit(dto.unit);
        p.setImageUrl(dto.imageUrl);
        p.setCategory(dto.category);
        p.setStock(dto.stock);

        repo.save(p);

        return map(p);
    }

    public List<ProductResponseDto> getAll() {
        return repo.findByDeletedFalse()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public ProductResponseDto getById(Long id) {
        Product p = repo.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (p.isDeleted()) {
            throw new ResourceNotFoundException("Product not found");
        }
        return map(p);
    }

    public ProductResponseDto update(Long id, ProductResponseDto dto) {
        Product p = repo.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (p.isDeleted()) {
            throw new ResourceNotFoundException("Product not found");
        }

        p.setName(dto.name);
        p.setPrice(dto.price);
        p.setUnit(dto.unit);
        p.setImageUrl(dto.imageUrl);
        p.setCategory(dto.category);
        p.setStock(dto.stock);

        repo.save(p);
        return map(p);
    }

    public void delete(Long id) {
        Product p = repo.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        p.setDeleted(true);
        repo.save(p);
    }

    private ProductResponseDto map(Product p) {
        return new ProductResponseDto(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getUnit(),
                p.getImageUrl(),
                p.getCategory(),
                p.getStock());
    }
}
