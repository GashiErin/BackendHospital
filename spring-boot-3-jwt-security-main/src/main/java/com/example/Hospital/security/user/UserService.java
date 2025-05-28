package com.example.Hospital.security.user;

import com.example.Hospital.security.exception.InvalidPasswordException;
import com.example.Hospital.security.token.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;

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
        tokenRepository.deleteAllByUserId(userId);
        repository.delete(user);
    }

    public User getCurrentUser(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getUsersByRole(Role role) {
        return repository.findByRole(role);
    }
}
