package com.example.Hospital.security.demo;

import com.example.Hospital.security.appointment.Appointment;
import com.example.Hospital.security.appointment.AppointmentService;
import com.example.Hospital.security.appointment.AppointmentStatus;
import com.example.Hospital.security.appointment.AppointmentType;
import com.example.Hospital.security.user.User;
import com.example.Hospital.security.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(
            @RequestParam Integer clientId,
            @RequestParam Integer professionalId,
            @RequestParam LocalDateTime appointmentDateTime,
            @RequestParam AppointmentType type,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(appointmentService.createAppointment(
                clientId, professionalId, appointmentDateTime, type, notes));
    }

    @GetMapping("/client")
    public ResponseEntity<List<Appointment>> getClientAppointments(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(appointmentService.getClientAppointments(user.getId()));
    }

    @GetMapping("/professional")
    public ResponseEntity<List<Appointment>> getProfessionalAppointments(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(appointmentService.getProfessionalAppointments(user.getId()));
    }

    @GetMapping("/client/upcoming")
    public ResponseEntity<List<Appointment>> getUpcomingClientAppointments(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(appointmentService.getUpcomingClientAppointments(user.getId()));
    }

    @GetMapping("/professional/upcoming")
    public ResponseEntity<List<Appointment>> getUpcomingProfessionalAppointments(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(appointmentService.getUpcomingProfessionalAppointments(user.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Integer id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Integer id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
