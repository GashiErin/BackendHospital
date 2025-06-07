package com.example.Hospital.security.review;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    // Find review by appointment ID
    Optional<Review> findByAppointmentIdAndIsDeletedFalse(Long appointmentId);
    
    // Find all active reviews for a professional
    @Query("{ 'professionalId': ?0, 'isDeleted': false }")
    List<Review> findByProfessionalId(Integer professionalId);
    
    // Find all active reviews by a patient
    @Query("{ 'patientId': ?0, 'isDeleted': false }")
    List<Review> findByPatientId(Integer patientId);
    
    // Find all active reviews for a professional of specific type
    @Query("{ 'professionalId': ?0, 'professionalType': ?1, 'isDeleted': false }")
    List<Review> findByProfessionalIdAndProfessionalType(Integer professionalId, String professionalType);
    
    // Calculate average rating for a professional
    @Query(value = "{ 'professionalId': ?0, 'isDeleted': false }", count = true)
    Double calculateAverageRating(Integer professionalId);
} 