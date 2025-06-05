package com.example.Hospital.security.demo;

import com.example.Hospital.security.appointment.AppointmentHistory;
import com.example.Hospital.security.appointment.AppointmentHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentHistoryController {
    private final AppointmentHistoryService historyService;

    public AppointmentHistoryController(AppointmentHistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/appointments/{appointmentId}/history")
    public ResponseEntity<AppointmentHistory> addHistory(
            @PathVariable Integer appointmentId,
            @RequestBody HistoryRequest request) {
        AppointmentHistory history = historyService.saveHistory(appointmentId, request.getHistoryText());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/appointments/{appointmentId}/history")
    public ResponseEntity<List<AppointmentHistory>> getAppointmentHistories(
            @PathVariable Integer appointmentId) {
        List<AppointmentHistory> histories = historyService.getHistoriesByAppointment(appointmentId);
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/clients/{clientId}/histories")
    public ResponseEntity<List<AppointmentHistory>> getClientHistories(
            @PathVariable Long clientId) {
        List<AppointmentHistory> histories = historyService.getHistoriesByClient(clientId);
        return ResponseEntity.ok(histories);
    }
}
