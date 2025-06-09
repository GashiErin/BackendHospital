package com.example.Hospital.security.user;

import com.example.Hospital.security.exception.InvalidPasswordException;
import com.example.Hospital.security.token.TokenRepository;
import com.example.Hospital.security.chat.MessageRepository;
import com.example.Hospital.security.chat.ChatRoomRepository;
import com.example.Hospital.security.appointment.AppointmentHistoryRepository;
import com.example.Hospital.security.appointment.AppointmentRepository;
import com.example.Hospital.security.appointment.Appointment;
import com.example.Hospital.security.appointment.AppointmentHistory;
import com.example.Hospital.security.notifications.NotificationRepository;
import com.example.Hospital.security.notifications.Notification;
import com.example.Hospital.security.payment.PaymentRepository;
import com.example.Hospital.security.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final AppointmentHistoryRepository appointmentHistoryRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationRepository notificationRepository;
    private final PaymentRepository paymentRepository;

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        if (request.getCurrentPassword() == null || request.getNewPassword() == null ||
                request.getConfirmationPassword() == null) {
            throw new InvalidPasswordException("All password fields are required");
        }

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        logger.debug("Processing password change for user: {}", user.getEmail());

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            logger.debug("Current password verification failed for user: {}", user.getEmail());
            throw new InvalidPasswordException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            logger.debug("New passwords do not match for user: {}", user.getEmail());
            throw new InvalidPasswordException("New passwords do not match");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            logger.debug("New password is same as current password for user: {}", user.getEmail());
            throw new InvalidPasswordException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        logger.info("Password successfully changed for user: {}", user.getEmail());
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User updateUserProfile(Integer id, User newUserData) {
        return repository.findById(id).map(user -> {
            if (newUserData.getFirstname() != null) user.setFirstname(newUserData.getFirstname());
            if (newUserData.getLastname() != null) user.setLastname(newUserData.getLastname());
            if (newUserData.getEmail() != null) user.setEmail(newUserData.getEmail());
            if (newUserData.getCity() != null) user.setCity(newUserData.getCity());
            if (newUserData.getCountry() != null) user.setCountry(newUserData.getCountry());
            if (newUserData.getAbout() != null) user.setAbout(newUserData.getAbout());
            return repository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Integer id, User newUserData) {
        return repository.findById(id).map(user -> {
            user.setFirstname(newUserData.getFirstname());
            user.setLastname(newUserData.getLastname());
            user.setEmail(newUserData.getEmail());
            return repository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void deleteUser(Integer userId) {
        var user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            logger.debug("Starting deletion process for user: {}", userId);

            // Delete all notifications where user is involved (as recipient, patient, or doctor)
            List<Notification> userNotifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
            List<Notification> patientNotifications = notificationRepository.findByPatientId(userId);
            List<Notification> doctorNotifications = notificationRepository.findByDoctorId(userId);
            
            notificationRepository.deleteAll(userNotifications);
            notificationRepository.deleteAll(patientNotifications);
            notificationRepository.deleteAll(doctorNotifications);
            logger.debug("Deleted notifications for user - recipient: {}, patient: {}, doctor: {}", 
                userNotifications.size(), patientNotifications.size(), doctorNotifications.size());

            // Delete all payment records for the user
            List<Payment> userPayments = paymentRepository.findByUserId(userId);
            paymentRepository.deleteAll(userPayments);
            logger.debug("Deleted {} payment records for user", userPayments.size());

            // Delete appointment histories and appointments
            List<Appointment> userAppointments = new ArrayList<>();
            userAppointments.addAll(user.getClientAppointments());
            userAppointments.addAll(user.getProfessionalAppointments());
            
            for (Appointment appointment : userAppointments) {
                // Delete appointment histories
                List<AppointmentHistory> histories = appointmentHistoryRepository.findByAppointmentId(appointment.getId().intValue());
                appointmentHistoryRepository.deleteAll(histories);
            }

            // Now safe to delete appointments
            appointmentRepository.deleteAll(userAppointments);

            // Delete chat rooms and messages
            var userChatRooms = chatRoomRepository.findByUser_Id(userId);
            for (var chatRoom : userChatRooms) {
                messageRepository.deleteAll(chatRoom.getMessages());
            }
            chatRoomRepository.deleteAll(userChatRooms);

            // Delete tokens
            tokenRepository.deleteAllByUserId(userId);

            // Finally delete the user
            repository.delete(user);
            logger.info("Successfully deleted user: {}", userId);

        } catch (Exception e) {
            logger.error("Error deleting user: {}", userId, e);
            throw e;
        }
    }

    public User getCurrentUser(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getUsersByRole(Role role) {
        return repository.findByRole(role);
    }
}