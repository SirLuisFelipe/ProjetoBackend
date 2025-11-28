package com.projeto.demo.controllers;

import com.projeto.demo.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class BaseControllerTest {

    private static class TestController extends BaseController {
        @Override
        protected User getLoggedUser() {
            return super.getLoggedUser();
        }

        @Override
        protected boolean isAdmin() {
            return super.isAdmin();
        }
    }

    private final TestController controller = new TestController();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getLoggedUser_ShouldReturnPrincipal() {
        User user = new User();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
        SecurityContextHolder.setContext(context);

        assertEquals(user, controller.getLoggedUser());
    }

    @Test
    void getLoggedUser_ShouldThrowWhenNoAuth() {
        SecurityContextHolder.clearContext();

        assertThrows(IllegalStateException.class, controller::getLoggedUser);
    }

    @Test
    void isAdmin_ShouldHandleRoles() {
        User user = new User();
        user.setRole("ROLE_ADMIN");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
        SecurityContextHolder.setContext(context);

        assertTrue(controller.isAdmin());
        user.setRole("user");
        assertFalse(controller.isAdmin());
    }
}
