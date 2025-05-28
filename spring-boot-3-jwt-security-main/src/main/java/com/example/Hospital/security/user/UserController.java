package com.example.Hospital.security.user;

import com.example.Hospital.security.exception.ApiResponse;
import com.example.Hospital.security.exception.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        try {
            if (connectedUser == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "User not authenticated"));
            }

            if (request == null || request.getCurrentPassword() == null ||
                    request.getNewPassword() == null || request.getConfirmationPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "All password fields are required"));
            }

            logger.debug("Password change request received for user: {}", connectedUser.getName());
            userService.changePassword(request, connectedUser);

            logger.info("Password successfully changed for user: {}", connectedUser.getName());
            return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));

        } catch (InvalidPasswordException e) {
            logger.warn("Password change failed for user {}: {}",
                    connectedUser != null ? connectedUser.getName() : "unknown", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during password change for user {}: {}",
                    connectedUser != null ? connectedUser.getName() : "unknown", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An unexpected error occurred"));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody User user
    ) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateOwnProfile(
            @RequestBody User updatedUserData,
            Principal principal
    ) {
        try {
            User currentUser = userService.getCurrentUser(principal.getName());
            User updatedUser = userService.updateUserProfile(currentUser.getId(), updatedUserData);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating profile: " + e.getMessage()));
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<User> updateUserProfileById(
            @PathVariable Integer id,
            @RequestBody User user
    ) {
        User updatedUser = userService.updateUserProfile(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
