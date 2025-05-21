package com.example.Hospital.security.user;

import com.example.Hospital.security.token.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }


    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User updateUser(Integer id, User newUserData) {
        return repository.findById(id).map(user -> {
            user.setFirstname(newUserData.getFirstname());
            user.setLastname(newUserData.getLastname());
            user.setEmail(newUserData.getEmail());
            // Only update password if you want, and remember to encode!
            // user.setPassword(passwordEncoder.encode(newUserData.getPassword()));
          //  user.setRole(newUserData.getRole());
            return repository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }



    @Transactional
    public void deleteUser(Integer userId) {
        var user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete all tokens for this user
        tokenRepository.deleteAllByUserId(userId);

        // Delete the user
        repository.delete(user);
    }

    public User getCurrentUser(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }







}