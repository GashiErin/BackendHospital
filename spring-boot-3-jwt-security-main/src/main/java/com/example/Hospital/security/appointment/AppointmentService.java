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
    private final com.example.Hospital.security.notifications.NotificationService notificationService;

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

        // Send notification to the professional
        String profMessage = String.format("New %s appointment scheduled with %s %s for %s",
                type.toString(),
                client.getFirstname(),
                client.getLastname(),
                appointmentDateTime.toString());
        notificationService.createNotification(professional, profMessage, com.example.Hospital.security.notifications.NotificationType.APPOINTMENT_BOOKED);

        // Send confirmation notification to the client
        String clientMessage = String.format("Your %s appointment with %s %s has been scheduled for %s",
                type.toString(),
                professional.getFirstname(),
                professional.getLastname(),
                appointmentDateTime.toString());
        notificationService.createNotification(client, clientMessage, com.example.Hospital.security.notifications.NotificationType.APPOINTMENT_BOOKED);

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

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);

        // Send notifications based on status update
        String message = String.format("Your appointment scheduled for %s has been marked as %s",
                appointment.getAppointmentDateTime().toString(),
                status.toString());

        if (status == AppointmentStatus.COMPLETED) {
            notificationService.createNotification(appointment.getClient(), message, com.example.Hospital.security.notifications.NotificationType.APPOINTMENT_UPDATED);
        } else if (status == AppointmentStatus.CANCELLED) {
            // Notify client
            notificationService.createNotification(appointment.getClient(), message, com.example.Hospital.security.notifications.NotificationType.APPOINTMENT_CANCELLED);

            // Notify professional
            String profMessage = String.format("Appointment with %s %s for %s has been cancelled",
                    appointment.getClient().getFirstname(),
                    appointment.getClient().getLastname(),
                    appointment.getAppointmentDateTime().toString());
            notificationService.createNotification(appointment.getProfessional(), profMessage, com.example.Hospital.security.notifications.NotificationType.APPOINTMENT_CANCELLED);
        }

        return appointment;
    }

    public void deleteAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Send cancellation notifications before deleting
        String clientMessage = String.format("Your appointment with %s %s scheduled for %s has been cancelled",
                appointment.getProfessional().getFirstname(),
                appointment.getProfessional().getLastname(),
                appointment.getAppointmentDateTime().toString());
        notificationService.createNotification(appointment.getClient(), clientMessage, com.example.Hospital.security.notifications.NotificationType.APPOINTMENT_CANCELLED);

        String profMessage = String.format("Appointment with %s %s scheduled for %s has been cancelled",
                appointment.getClient().getFirstname(),
                appointment.getClient().getLastname(),
                appointment.getAppointmentDateTime().toString());
        notificationService.createNotification(appointment.getProfessional(), profMessage, com.example.Hospital.security.notifications.NotificationType.APPOINTMENT_CANCELLED);

        appointmentRepository.deleteById(appointmentId);
    }
}