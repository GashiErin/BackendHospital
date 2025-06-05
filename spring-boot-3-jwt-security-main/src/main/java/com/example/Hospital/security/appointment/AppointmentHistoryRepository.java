package com.example.Hospital.security.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, Long> {
    List<AppointmentHistory> findByAppointmentId(Integer appointmentId);
    List<AppointmentHistory> findByAppointment_Client_Id(Long clientId);
}
