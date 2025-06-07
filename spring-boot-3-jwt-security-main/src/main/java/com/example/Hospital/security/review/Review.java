package com.example.Hospital.security.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private Long appointmentId;  // Reference to the appointment
    private Integer patientId;   // User ID of the patient
    private Integer professionalId; // ID of the therapist/nutritionist
    private String professionalType; // "THERAPIST" or "NUTRICIST"
    private Integer rating;      // Rating out of 5
    private String comment;      // Review comment
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;   // Soft delete flag
} 