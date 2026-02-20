package com.krish.AgariBackend.controller;

import com.krish.AgariBackend.service.AiAdvisoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

// Allow frontend
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiAdvisoryController {

    @Autowired
    private AiAdvisoryService aiAdvisoryService;

    @PostMapping("/chat")
    public ResponseEntity<String> chatWithAi(@RequestBody Map<String, String> payload) {
        // Extracts only the value from the "message" key
        String userText = payload.get("message");
        String response = aiAdvisoryService.chatWithAi(userText);
        return ResponseEntity.ok(response);
    }
}
