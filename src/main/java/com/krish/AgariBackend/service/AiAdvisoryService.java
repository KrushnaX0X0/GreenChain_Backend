package com.krish.AgariBackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class AiAdvisoryService {

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // List of active free models to try in order
    private static final List<String> MODELS = List.of(
            "google/gemini-2.0-flash:free",
            "google/gemini-1.5-flash:free",
            "meta-llama/llama-3.3-70b-instruct:free");

    public String chatWithAi(String message) {
        Exception lastError = null;
        for (String model : MODELS) {
            try {
                return callOpenRouter(message, model);
            } catch (Exception e) {
                System.err.println("⚠️ Model " + model + " failed: " + e.getMessage());
                lastError = e;
            }
        }
        return "AI Service Error: All models are currently unavailable. Last error: "
                + (lastError != null ? lastError.getMessage() : "None");
    }

    private String callOpenRouter(String message, String modelId) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (openRouterApiKey == null) {
            throw new IllegalStateException("OpenRouter API key is not configured");
        }
        headers.setBearerAuth(openRouterApiKey);
        headers.set("HTTP-Referer", "http://localhost:8080"); // Required for free models
        headers.set("X-Title", "AgariApp");

        Map<String, Object> payload = Map.of(
                "model", modelId,
                "messages", List.of(Map.of("role", "user", "content", message)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        return parseContent(response.getBody());
    }

    private String parseContent(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Parsing failed");
        }
    }
}