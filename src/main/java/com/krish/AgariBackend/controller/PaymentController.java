package com.krish.AgariBackend.controller;

// import com.krish.AgariBackend.dto.payment.PaymentVerifyDto;
import com.krish.AgariBackend.dto.payment.RazorpayOrderRequestDto;
import com.krish.AgariBackend.service.RazorpayService;
import com.razorpay.Order;
// import com.razorpay.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import com.krish.AgariBackend.entity.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final RazorpayService razorpayService;
    private final com.krish.AgariBackend.repo.PaymentRepository paymentRepository;
    private final com.krish.AgariBackend.repo.UserRepository userRepository;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    public PaymentController(RazorpayService razorpayService,
            com.krish.AgariBackend.repo.PaymentRepository paymentRepository,
            com.krish.AgariBackend.repo.UserRepository userRepository) {
        this.razorpayService = razorpayService;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    /**
     * STEP 1: Create a Razorpay Order
     * Called when the user clicks "Pay" on the frontend.
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody RazorpayOrderRequestDto dto) {
        try {
            System.out.println("Initiating Payment Order via Razorpay");
            System.out.println("Amount Request: " + dto.getAmount());
            Order order = razorpayService.createRazorpayOrder(dto.getAmount());

            // Save Payment Details to Database
            com.krish.AgariBackend.entity.Payment payment = new com.krish.AgariBackend.entity.Payment();
            payment.setRazorpayOrderId(order.get("id"));
            payment.setStatus("CREATED");
            payment.setAmount(dto.getAmount());

            // Get Authenticated User
            org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String email = auth.getName();
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    payment.setUser(user);
                }
            }

            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", order.get("id"));
            response.put("amount", order.get("amount")); // already in paise
            response.put("currency", order.get("currency"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}