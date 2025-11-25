package com.projeto.demo.controllers;

import com.projeto.demo.entities.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected User getLoggedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }
        throw new IllegalStateException("User not authenticated");
    }

    protected boolean isAdmin() {
        User user = getLoggedUser();
        String role = user.getRole();
        if (role == null) {
            return false;
        }
        if (role.toUpperCase().startsWith("ROLE_")) {
            role = role.substring(5);
        }
        return "ADMIN".equalsIgnoreCase(role);
    }
}
