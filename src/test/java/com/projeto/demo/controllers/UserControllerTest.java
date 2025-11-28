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
}
