package com.krish.AgariBackend.service;

import com.krish.AgariBackend.dto.SupportTicketDto;
import com.krish.AgariBackend.entity.Order;
import com.krish.AgariBackend.entity.SupportTicket;
import com.krish.AgariBackend.entity.User;
import com.krish.AgariBackend.repo.OrderRepository;
import com.krish.AgariBackend.repo.SupportTicketRepository;
import com.krish.AgariBackend.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public SupportTicketService(SupportTicketRepository ticketRepository, UserRepository userRepository,
            OrderRepository orderRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public SupportTicket createTicket(SupportTicketDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketType(dto.getTicketType());
        ticket.setDescription(dto.getDescription());
        ticket.setUser(user);

        if (dto.getOrderId() != null) {
            Order order = orderRepository.findById(java.util.Objects.requireNonNull(dto.getOrderId())).orElse(null);
            ticket.setOrder(order);
        }

        return ticketRepository.save(ticket);
    }

    public List<SupportTicket> getUserTickets() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null)
            return Collections.emptyList();

        return ticketRepository.findByUserId(user.getId());
    }

    // 🔐 ADMIN: Get All Tickets
    public List<SupportTicket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // 🔐 ADMIN: Update Status
    public SupportTicket updateTicketStatus(Long id, String status) {
        SupportTicket ticket = ticketRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    // 🔐 ADMIN: Update Response
    public SupportTicket updateTicketResponse(Long id, String response) {
        SupportTicket ticket = ticketRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setAdminResponse(response);
        // Auto-update status if not already closed
        if (!"CLOSED".equals(ticket.getStatus())) {
            ticket.setStatus("IN_REVIEW");
        }
        return ticketRepository.save(ticket);
    }

    // 🔐 User: Delete Ticket
    public void deleteTicket(Long id) {
        SupportTicket ticket = ticketRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        System.out.println("🗑️ Attempting to delete ticket: " + id);

        // Ensure user owns the ticket OR is an ADMIN
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("👤 Current User: " + email);

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        System.out.println("🛡️ Is Admin: " + isAdmin);

        // Handle case where ticket user might be null (e.g. deleted user)
        if (ticket.getUser() == null) {
            System.out.println("⚠️ Ticket has no owner");
            // If ticket has no user, only Admin can delete
            if (!isAdmin) {
                System.out.println("❌ Unauthorized: Non-admin trying to delete orphan ticket");
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.FORBIDDEN,
                        "Unauthorized: Ticket has no owner, only Admin can delete");
            }
            ticketRepository.delete(ticket);
            System.out.println("✅ Orphan ticket deleted");
            return;
        }

        System.out.println("👤 Ticket Owner: " + ticket.getUser().getEmail());

        if (!isAdmin && !ticket.getUser().getEmail().equals(email)) {
            System.out.println("❌ Unauthorized: User mismatch");
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Unauthorized to delete this ticket");
        }

        try {
            ticketRepository.delete(ticket);
            System.out.println("✅ Ticket deleted successfully");
        } catch (Exception e) {
            System.out.println("❌ DELETE FAILED");
            e.printStackTrace();
            throw e;
        }
    }
}
