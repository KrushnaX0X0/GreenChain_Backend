package com.krish.AgariBackend.repo;

//package com.agarimart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krish.AgariBackend.entity.Payment;
//import com.agarimart.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}

