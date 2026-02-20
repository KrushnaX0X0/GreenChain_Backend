package com.krish.AgariBackend.controller;

import com.krish.AgariBackend.dto.SupportTicketDto;
import com.krish.AgariBackend.entity.SupportTicket;
import com.krish.AgariBackend.service.SupportTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support/tickets")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<SupportTicket> createTicket(@RequestBody SupportTicketDto dto) {
        return ResponseEntity.ok(ticketService.createTicket(dto));
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<SupportTicket>> getMyTickets() {
        return ResponseEntity.ok(ticketService.getUserTickets());
    }

    // 🔐 ADMIN: Get All Tickets
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // 🔐 ADMIN: Update Status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<SupportTicket> updateTicketStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }

    // 🔐 ADMIN: Update Response
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/respond")
    public ResponseEntity<SupportTicket> respondToTicket(@PathVariable Long id,
            @RequestBody java.util.Map<String, String> payload) {
        String response = payload.get("response");
        return ResponseEntity.ok(ticketService.updateTicketResponse(id, response));
    }

    // 🔐 User: Delete Ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
