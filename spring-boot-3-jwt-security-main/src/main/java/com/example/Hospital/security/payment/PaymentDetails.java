package com.example.Hospital.security.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PaymentDetails {
    private String cardHolderName;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
}