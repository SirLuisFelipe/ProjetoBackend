package com.projeto.demo.controllers;

import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.entities.User;
import com.projeto.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserController controllerSpy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controllerSpy = Mockito.spy(userController);
    }

    @Test
    void update_ShouldReturnUser() {
        UserRegisterDto dto = new UserRegisterDto();
        User updated = new User();
        when(userService.update(dto)).thenReturn(updated);

        ResponseEntity<?> response = controllerSpy.update(dto);

        assertEquals(updated, response.getBody());
        verify(userService).update(dto);
    }

    @Test
    void listUsers_ShouldReturnFromService() {
        when(userService.listUsers()).thenReturn(List.of(new User()));

        ResponseEntity<?> response = controllerSpy.listUsers();

        assertEquals(1, ((List<?>) response.getBody()).size());
        verify(userService).listUsers();
    }

    @Test
    void deleteById_ShouldRequireAdmin() {
        doReturn(true).when(controllerSpy).isAdmin();

        ResponseEntity<?> response = controllerSpy.deleteById(5L);

        assertEquals(200, response.getStatusCode().value());
        verify(userService).deleteById(5L);
    }
}
