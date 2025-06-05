package com.example.Hospital.security.payment;

import com.example.Hospital.security.payment.PaymentDetails;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Integer userId;
    private BigDecimal amount;
    private PaymentDetails paymentDetails;
}