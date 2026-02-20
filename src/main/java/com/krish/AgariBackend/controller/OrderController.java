package com.krish.AgariBackend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// import com.krish.AgariBackend.dto.*;
import com.krish.AgariBackend.dto.order.OrderRequestDto;
import com.krish.AgariBackend.dto.order.OrderResponseDto;
import com.krish.AgariBackend.entity.User;
import com.krish.AgariBackend.repo.UserRepository;
import com.krish.AgariBackend.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService service;
    private final UserRepository userRepository;

    public OrderController(OrderService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    // PLACE ORDER
    @PostMapping
    public Object placeOrder(
            @RequestBody OrderRequestDto dto,
            Authentication authentication) {

        User user = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        // If user is null, service will create a guest order (user field will be null)
        return service.createOrder(user, dto);
    }

    // USER ORDERS
    @GetMapping("/my-orders")
    public List<OrderResponseDto> myOrders(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return service.getUserOrders(user);
    }

    // ALL ORDERS (ADMIN ONLY)
    @GetMapping("/all")
    public List<OrderResponseDto> getAllOrders(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Access Denied: Admins Only");
        }

        return service.getAllOrders();
    }

    // UPDATE STATUS (ADMIN)
    // UPDATE STATUS (ADMIN)
    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Access Denied: Admins Only");
        }

        String status = body.get("status");
        service.updateOrderStatus(id, status);
    }

    // DELETE ORDER (ADMIN)
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Access Denied: Admins Only");
        }

        service.deleteOrder(id);
    }

    @DeleteMapping("/my-orders/{id}")
    public void deleteMyOrder(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        service.deleteUserOrder(id, email);
    }
}