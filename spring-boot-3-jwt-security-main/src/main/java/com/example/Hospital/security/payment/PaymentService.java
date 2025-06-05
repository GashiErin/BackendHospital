package com.example.Hospital.security.payment;

import com.example.Hospital.security.exception.ResourceNotFoundException;
import com.example.Hospital.security.user.User;
import com.example.Hospital.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;






    // Add this method
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }


    @Transactional
    public Payment processPayment(PaymentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create payment record
        Payment payment = Payment.builder()
                .user(user)
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .lastFourDigits(request.getPaymentDetails().getCardNumber()
                        .substring(request.getPaymentDetails().getCardNumber().length() - 4))
                .status(PaymentStatus.PAID)
                .cardHolderName(request.getPaymentDetails().getCardHolderName())
                .expiryDate(request.getPaymentDetails().getExpiryDate())
                .cvv(request.getPaymentDetails().getCvv())
                .build();

        // Add credits to user
        user.setCredits(user.getCredits() + request.getAmount().intValue());
        userRepository.save(user);

        return paymentRepository.save(payment);
    }

    public Integer getUserCredits(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getCredits();
    }
}