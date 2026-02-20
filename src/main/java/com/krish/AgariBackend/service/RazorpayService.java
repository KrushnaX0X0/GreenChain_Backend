package com.krish.AgariBackend.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public Order createRazorpayOrder(Double amount) throws RazorpayException {
        try {
            // Initialize Razorpay client
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            // Convert amount to paise (1 INR = 100 Paise)
            // Using Math.round to ensure no precision loss from Double
            long amountInPaise = Math.round(amount * 100);

            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            // Create and return the order
            return client.orders.create(options);

        } catch (RazorpayException e) {
            // Log the error and rethrow or handle specifically
            System.err.println("Razorpay Order Creation Failed: " + e.getMessage());
            throw e;
        }
    }
}