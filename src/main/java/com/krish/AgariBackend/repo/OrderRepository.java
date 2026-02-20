package com.krish.AgariBackend.repo;

//package com.agarimart.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.krish.AgariBackend.entity.Order;
//import com.agarimart.entity.*;
import com.krish.AgariBackend.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
//    List<Order> findByUser(User user);
}

