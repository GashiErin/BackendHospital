package com.example.Hospital.security.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;


    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can access all payments
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }


    @GetMapping("/users/{userId}/credits")
    public ResponseEntity<Integer> getUserCredits(@PathVariable Integer userId) {
        log.info("Fetching credits for user: {}", userId);
        try {
            Integer credits = paymentService.getUserCredits(userId);
            log.info("Credits found for user {}: {}", userId, credits);
            return ResponseEntity.ok(credits);
        } catch (Exception e) {
            log.error("Error fetching credits for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/payments/process")
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentRequest request) {
        log.info("Processing payment for user: {}, amount: {}", request.getUserId(), request.getAmount());
        try {
            Payment payment = paymentService.processPayment(request);
            log.info("Payment processed successfully for user {}", request.getUserId());
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Error processing payment for user {}: {}", request.getUserId(), e.getMessage());
            throw e;
        }
    }
}