package com.krish.AgariBackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.krish.AgariBackend.dto.order.OrderItemRequestDto;
import com.krish.AgariBackend.dto.order.OrderItemResponseDto;
import com.krish.AgariBackend.dto.order.OrderRequestDto;
import com.krish.AgariBackend.dto.order.OrderResponseDto;
import com.krish.AgariBackend.entity.*;
import com.krish.AgariBackend.repo.*;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final EmailService emailService;
    private final InvoiceService invoiceService;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo, EmailService emailService,
            InvoiceService invoiceService) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.emailService = emailService;
        this.invoiceService = invoiceService;
    }

    // PLACE ORDER
    public Order createOrder(User user, OrderRequestDto dto) {
        Order order = new Order();
        order.setUser(user);
        order.setMobile(dto.getMobile());
        order.setAddress(dto.getAddress());
        order.setCity(dto.getCity());

        double totalAmount = 0;
        List<OrderItem> items = new ArrayList<>();

        // Process items
        for (int i = 0; i < dto.getItems().size(); i++) {
            OrderItemRequestDto itemDto = dto.getItems().get(i);

            Product product = productRepo
                    .findById(java.util.Objects.requireNonNull(itemDto.getProductId(), "Product ID cannot be null"))
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Check stock
            if (product.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Deduct stock
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepo.save(product);

            // Set the product_id for the 'orders' table.
            if (i == 0) {
                order.setProduct(product);
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(product.getPrice());
            item.setTotalPrice(product.getPrice() * itemDto.getQuantity());

            totalAmount += item.getTotalPrice();
            items.add(item);
        }

        order.setTotalPrice(totalAmount);
        order.setItems(items);

        Order savedOrder = orderRepo.save(order);

        // Send Email (Async)
        try {
            if (user != null && user.getEmail() != null) {
                byte[] invoicePdf = invoiceService.generateInvoicePdf(savedOrder);
                emailService.sendOrderConfirmation(
                        user.getEmail(),
                        "AgriMart Order Confirmation #" + savedOrder.getId(),
                        "Dear " + (user.getUsername() != null ? user.getUsername() : "Customer") + ",\n\n" +
                                "Thank you for shopping with us! Your order has been placed successfully.\n" +
                                "Please find the invoice attached.\n\n" +
                                "Best Regards,\nAgriMart Team",
                        invoicePdf,
                        "Invoice_" + savedOrder.getId() + ".pdf");
            }
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Don't fail the order if email fails
        }

        return savedOrder;
    }

    // USER ORDERS
    public List<OrderResponseDto> getUserOrders(User user) {
        return orderRepo.findByUser(user)
                .stream()
                .map(order -> {
                    // map OrderItem → OrderItemResponseDto
                    List<OrderItemResponseDto> items = order.getItems().stream()
                            .map(item -> {
                                OrderItemResponseDto dto = new OrderItemResponseDto();
                                dto.setProductName(item.getProduct().getName());
                                dto.setQuantity(item.getQuantity());
                                dto.setPrice(item.getPrice());
                                dto.setTotalPrice(item.getTotalPrice());
                                return dto;
                            })
                            .collect(Collectors.toList());

                    // map Order → OrderResponseDto
                    return new OrderResponseDto(
                            order.getId(),
                            order.getTotalPrice(),
                            order.getCity(),
                            order.getUser() != null ? order.getUser().getUsername() : "Guest",
                            order.getMobile(),
                            order.getAddress(),
                            order.getStatus(),
                            order.getOrderDate() != null ? order.getOrderDate().toString() : "",
                            items);
                })
                .collect(Collectors.toList());
    }

    // ALL ORDERS (ADMIN)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepo.findAll()
                .stream()
                .map(order -> {
                    // map OrderItem → OrderItemResponseDto
                    List<OrderItemResponseDto> items = order.getItems().stream()
                            .map(item -> {
                                OrderItemResponseDto dto = new OrderItemResponseDto();
                                dto.setProductName(item.getProduct().getName());
                                dto.setQuantity(item.getQuantity());
                                dto.setPrice(item.getPrice());
                                dto.setTotalPrice(item.getTotalPrice());
                                return dto;
                            })
                            .collect(Collectors.toList());

                    // map Order → OrderResponseDto
                    return new OrderResponseDto(
                            order.getId(),
                            order.getTotalPrice(),
                            order.getCity(),
                            order.getUser() != null ? order.getUser().getUsername() : "Guest",
                            order.getMobile(),
                            order.getAddress(),
                            order.getStatus(),
                            order.getOrderDate() != null ? order.getOrderDate().toString() : "",
                            items);
                })
                .collect(Collectors.toList());
    }

    // UPDATE STATUS
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepo.findById(java.util.Objects.requireNonNull(orderId, "Order ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepo.save(order);
    }

    // DELETE ORDER
    public void deleteOrder(Long orderId) {
        Long nonNullId = java.util.Objects.requireNonNull(orderId, "Order ID cannot be null");
        if (!orderRepo.existsById(nonNullId)) {
            throw new RuntimeException("Order not found");
        }
        orderRepo.deleteById(nonNullId);
    }

    // DELETE USER ORDER
    public void deleteUserOrder(Long orderId, String userEmail) {
        Order order = orderRepo.findById(java.util.Objects.requireNonNull(orderId, "Order ID cannot be null"))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getUser() == null || !order.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access Denied: You can only delete your own orders");
        }

        orderRepo.deleteById(orderId);
    }
}