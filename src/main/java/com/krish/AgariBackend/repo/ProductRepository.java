package com.krish.AgariBackend.repo;
//package com.agarimart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krish.AgariBackend.entity.Product;
import java.util.List;
//import com.agarimart.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedFalse();
}
