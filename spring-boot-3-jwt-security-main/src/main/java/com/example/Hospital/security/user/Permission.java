package com.example.Hospital.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete"),

    THERAPIST_READ("therapist:read"),
    THERAPIST_UPDATE("therapist:update"),
    THERAPIST_CREATE("therapist:create"),
    THERAPIST_DELETE("therapist:delete")

    ;

    private final String permission;
}
