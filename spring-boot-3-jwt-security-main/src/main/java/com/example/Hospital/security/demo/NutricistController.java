package com.example.Hospital.security.demo;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/nutricist")
@PreAuthorize("hasRole('ADMIN')")
public class NutricistController {

    @GetMapping
    @PreAuthorize("hasAuthority('nutricist:read')")
    public String get() {
        return "GET:: nutricist controller";
    }
    @PostMapping
    @PreAuthorize("hasAuthority('nutricist:create')")
    @Hidden
    public String post() {
        return "POST:: nutricist controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority('nutricist:update')")
    @Hidden
    public String put() {
        return "PUT:: nutricist controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('nutricist:delete')")
    @Hidden
    public String delete() {
        return "DELETE:: nutricist controller";
    }
}