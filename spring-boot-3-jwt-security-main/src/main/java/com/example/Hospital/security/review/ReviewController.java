package com.example.Hospital.security.review;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.Hospital.security.user.User;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Review> createReview(
            @PathVariable Long appointmentId,
            @RequestBody Review review,
            @AuthenticationPrincipal User user) {
        // Validate rating is between 1 and 5
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        review.setAppointmentId(appointmentId);
        review.setPatientId(user.getId());
        return ResponseEntity.ok(reviewService.createReview(review));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Review> updateReview(
            @PathVariable String reviewId,
            @RequestBody Review review,
            @AuthenticationPrincipal User user) {
        // Validate rating is between 1 and 5
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review existingReview = reviewService.getReviewById(reviewId);
        if (!existingReview.getPatientId().equals(user.getId())) {
            throw new IllegalStateException("You can only update your own reviews");
        }

        return ResponseEntity.ok(reviewService.updateReview(reviewId, review));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String reviewId,
            @AuthenticationPrincipal User user) {
        Review review = reviewService.getReviewById(reviewId);
        if (!review.getPatientId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own reviews");
        }

        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Review> getReviewByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(reviewService.getReviewByAppointment(appointmentId));
    }

    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<Review>> getReviewsByProfessional(
            @PathVariable Integer professionalId,
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(reviewService.getReviewsByProfessionalAndType(professionalId, type));
        }
        return ResponseEntity.ok(reviewService.getReviewsByProfessional(professionalId));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Review>> getReviewsByPatient(
            @PathVariable Integer patientId,
            @AuthenticationPrincipal User user) {
        // Users can only view their own reviews
        if (!patientId.equals(user.getId())) {
            throw new IllegalStateException("You can only view your own reviews");
        }
        return ResponseEntity.ok(reviewService.getReviewsByPatient(patientId));
    }

    @GetMapping("/professional/{professionalId}/rating")
    public ResponseEntity<Double> getProfessionalRating(@PathVariable Integer professionalId) {
        return ResponseEntity.ok(reviewService.getProfessionalAverageRating(professionalId));
    }
}