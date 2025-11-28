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

        ResponseEntity<User> response = controllerSpy.update(dto);

        assertEquals(updated, response.getBody());
        verify(userService).update(dto);
    }

    @Test
    void listUsers_ShouldReturnFromService() {
        when(userService.listUsers()).thenReturn(List.of(new User()));

        ResponseEntity<List<User>> response = controllerSpy.listUsers();

        assertEquals(1, response.getBody().size());
        verify(userService).listUsers();
    }

    @Test
    void deleteById_ShouldRequireAdmin() {
        doReturn(true).when(controllerSpy).isAdmin();

        ResponseEntity<Void> response = controllerSpy.deleteById(5L);

        assertEquals(200, response.getStatusCode().value());
        verify(userService).deleteById(5L);
    }

    @Test
    void deleteById_ShouldReturnForbiddenWhenNotAdmin() {
        doReturn(false).when(controllerSpy).isAdmin();

        ResponseEntity<Void> response = controllerSpy.deleteById(10L);

        assertEquals(403, response.getStatusCode().value());
        verify(userService, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_ShouldReturnServerErrorWhenServiceFails() {
        doReturn(true).when(controllerSpy).isAdmin();
        doThrow(new RuntimeException("fail")).when(userService).deleteById(20L);

        ResponseEntity<Void> response = controllerSpy.deleteById(20L);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void searchUsers_ShouldReturnResult() {
        when(userService.searchUsersByName("ana")).thenReturn(List.of(new User()));

        ResponseEntity<List<User>> response = controllerSpy.searchUsers("ana");

        assertEquals(1, response.getBody().size());
        verify(userService).searchUsersByName("ana");
    }

    @Test
    void findById_ShouldReturnUser() {
        User user = new User();
        when(userService.findById(3L)).thenReturn(user);

        ResponseEntity<User> response = controllerSpy.findById(3L);

        assertEquals(user, response.getBody());
        verify(userService).findById(3L);
    }

    @Test
    void update_ShouldReturnServerErrorWhenServiceFails() {
        UserRegisterDto dto = new UserRegisterDto();
        when(userService.update(dto)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<User> response = controllerSpy.update(dto);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void listUsers_ShouldReturnServerErrorWhenServiceFails() {
        when(userService.listUsers()).thenThrow(new RuntimeException("fail"));

        ResponseEntity<List<User>> response = controllerSpy.listUsers();

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void searchUsers_ShouldReturnServerErrorWhenServiceFails() {
        when(userService.searchUsersByName("ana")).thenThrow(new RuntimeException("fail"));

        ResponseEntity<List<User>> response = controllerSpy.searchUsers("ana");

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void findById_ShouldReturnServerErrorWhenServiceFails() {
        when(userService.findById(3L)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<User> response = controllerSpy.findById(3L);

        assertEquals(500, response.getStatusCode().value());
    }
}
