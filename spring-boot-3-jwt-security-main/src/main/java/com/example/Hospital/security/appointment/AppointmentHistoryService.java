package com.example.Hospital.security.appointment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppointmentHistoryService {
    private final AppointmentHistoryRepository historyRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentHistoryService(
            AppointmentHistoryRepository historyRepository,
            AppointmentRepository appointmentRepository) {
        this.historyRepository = historyRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public AppointmentHistory saveHistory(Integer appointmentId, String historyText) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        AppointmentHistory history = new AppointmentHistory();
        history.setAppointment(appointment);
        history.setHistoryText(historyText);
        history.setCreatedAt(LocalDateTime.now());

        return historyRepository.save(history);
    }

    public List<AppointmentHistory> getHistoriesByAppointment(Integer appointmentId) {
        return historyRepository.findByAppointmentId(appointmentId);
    }

    public List<AppointmentHistory> getHistoriesByClient(Long clientId) {
        return historyRepository.findByAppointment_Client_Id(clientId);
    }
}
