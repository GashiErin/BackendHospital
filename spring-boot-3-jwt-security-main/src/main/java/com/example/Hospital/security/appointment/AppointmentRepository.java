package com.example.Hospital.security.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByClientId(Integer clientId);
    List<Appointment> findByProfessionalId(Integer professionalId);
    boolean existsByProfessionalIdAndAppointmentDateTime(Integer professionalId, LocalDateTime dateTime);
    List<Appointment> findByClientIdAndAppointmentDateTimeGreaterThanEqual(Integer clientId, LocalDateTime dateTime);
    List<Appointment> findByProfessionalIdAndAppointmentDateTimeGreaterThanEqual(Integer professionalId, LocalDateTime dateTime);
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.client.id = :userId OR a.professional.id = :userId")
    boolean hasAppointments(@Param("userId") Integer userId);

}