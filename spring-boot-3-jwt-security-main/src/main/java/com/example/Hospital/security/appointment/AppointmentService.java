package com.example.Hospital.security.appointment;

import com.example.Hospital.security.user.User;
import com.example.Hospital.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public Appointment createAppointment(Integer clientId, Integer professionalId,
                                         LocalDateTime appointmentDateTime, AppointmentType type, String notes) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        // Add debug logging
        System.out.println("Creating appointment with:");
        System.out.println("Client ID: " + clientId);
        System.out.println("Professional ID: " + professionalId);
        System.out.println("DateTime: " + appointmentDateTime);
        System.out.println("Type: " + type);

        // Check if the professional already has an appointment at this time
        if (appointmentRepository.existsByProfessionalIdAndAppointmentDateTime(
                professional.getId(), appointmentDateTime)) {
            throw new RuntimeException("Professional is not available at this time");
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .professional(professional)
                .appointmentDateTime(appointmentDateTime)
                .type(type)
                .status(AppointmentStatus.SCHEDULED)
                .notes(notes)
                .createdAt(LocalDateTime.now())
                .build();

        // Add debug logging for saved appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);
        System.out.println("Saved appointment with ID: " + savedAppointment.getId());

        return savedAppointment;
    }

    public List<Appointment> getClientAppointments(Integer clientId) {
        // Add debug logging
        System.out.println("Fetching appointments for client ID: " + clientId);
        List<Appointment> appointments = appointmentRepository.findByClientId(clientId);
        System.out.println("Found " + appointments.size() + " appointments");
        return appointments;
    }

    public List<Appointment> getProfessionalAppointments(Integer professionalId) {
        return appointmentRepository.findByProfessionalId(professionalId);
    }

    public List<Appointment> getUpcomingClientAppointments(Integer clientId) {
        return appointmentRepository.findByClientIdAndAppointmentDateTimeGreaterThanEqual(
                clientId, LocalDateTime.now());
    }

    public List<Appointment> getUpcomingProfessionalAppointments(Integer professionalId) {
        return appointmentRepository.findByProfessionalIdAndAppointmentDateTimeGreaterThanEqual(
                professionalId, LocalDateTime.now());
    }

    public Appointment updateAppointmentStatus(Integer appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Integer appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }
}