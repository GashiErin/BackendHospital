package com.example.Hospital.security.review;

import com.example.Hospital.security.appointment.AppointmentService;
import com.example.Hospital.security.appointment.Appointment;
import com.example.Hospital.security.appointment.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final AppointmentService appointmentService;

    public Review createReview(Review review) {
        // Check if review already exists for this appointment
        reviewRepository.findByAppointmentIdAndIsDeletedFalse(review.getAppointmentId())
                .ifPresent(existingReview -> {
                    throw new IllegalStateException("Review already exists for this appointment");
                });

        // Validate that the appointment exists and is completed
        Appointment appointment = appointmentService.getAppointmentById(review.getAppointmentId().intValue());
        if (appointment == null) {
            throw new IllegalStateException("Appointment not found");
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot review an appointment that is not completed");
        }

        // Validate that the review is being created by the patient who had the appointment
        if (!appointment.getClient().getId().equals(review.getPatientId())) {
            throw new IllegalStateException("Only the patient who had the appointment can create a review");
        }

        // Set professional details from appointment
        review.setProfessionalId(appointment.getProfessional().getId());
        review.setProfessionalType(appointment.getType().toString());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setDeleted(false);

        return reviewRepository.save(review);
    }

    public Review getReviewById(String reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalStateException("Review not found"));
    }

    public Review updateReview(String reviewId, Review updatedReview) {
        return reviewRepository.findById(reviewId)
                .map(existingReview -> {
                    if (existingReview.isDeleted()) {
                        throw new IllegalStateException("Review has been deleted");
                    }

                    existingReview.setRating(updatedReview.getRating());
                    existingReview.setComment(updatedReview.getComment());
                    existingReview.setUpdatedAt(LocalDateTime.now());
                    return reviewRepository.save(existingReview);
                })
                .orElseThrow(() -> new IllegalStateException("Review not found"));
    }

    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalStateException("Review not found"));

        review.setDeleted(true);
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    public Review getReviewByAppointment(Long appointmentId) {
        return reviewRepository.findByAppointmentIdAndIsDeletedFalse(appointmentId)
                .orElseThrow(() -> new IllegalStateException("Review not found"));
    }

    public List<Review> getReviewsByProfessional(Integer professionalId) {
        return reviewRepository.findByProfessionalId(professionalId);
    }

    public List<Review> getReviewsByPatient(Integer patientId) {
        return reviewRepository.findByPatientId(patientId);
    }

    public List<Review> getReviewsByProfessionalAndType(Integer professionalId, String professionalType) {
        return reviewRepository.findByProfessionalIdAndProfessionalType(professionalId, professionalType);
    }

    public Double getProfessionalAverageRating(Integer professionalId) {
        return reviewRepository.calculateAverageRating(professionalId);
    }
}