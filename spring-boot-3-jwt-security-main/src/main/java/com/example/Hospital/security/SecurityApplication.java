package com.example.Hospital.security;

import com.example.Hospital.security.auth.AuthenticationRequest;
import com.example.Hospital.security.auth.AuthenticationService;
import com.example.Hospital.security.auth.RegisterRequest;
import com.example.Hospital.security.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static com.example.Hospital.security.user.Role.ADMIN;
import static com.example.Hospital.security.user.Role.MANAGER;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service,
			UserRepository userRepository
	) {
		return args -> {
			if (userRepository.findByEmail("admin@mail.com").isEmpty()) {
				var admin = RegisterRequest.builder()
						.firstname("Admin")
						.lastname("Admin")
						.email("admin@mail.com")
						.password("password")
						.role(ADMIN)
						.build();
				System.out.println("Admin token: " + service.register(admin).getAccessToken());
			} else {
				System.out.println("Admin already exists");
				var loginRequest = AuthenticationRequest.builder()
						.email("admin@mail.com")
						.password("password")
						.build();
				System.out.println("Admin token: " + service.authenticate(loginRequest).getAccessToken());
			}


		};
	}
}