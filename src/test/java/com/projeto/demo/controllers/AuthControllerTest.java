package com.projeto.demo.controllers;

import com.projeto.demo.config.JwtTokenProvider;
import com.projeto.demo.dto.UserLoginDto;
import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.entities.User;
import com.projeto.demo.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnToken() {
        UserRegisterDto dto = new UserRegisterDto();
        User user = new User();

        when(userService.register(dto)).thenReturn(user);
        when(jwtTokenProvider.gerarToken(user)).thenReturn("token");

        ResponseEntity<?> response = authController.register(dto);

        assertEquals("token", ((com.projeto.demo.dto.AuthResponseDto) response.getBody()).getToken());
    }

    @Test
    void login_ShouldReturnToken() {
        UserLoginDto dto = new UserLoginDto();
        User user = new User();

        when(userService.authenticate(dto)).thenReturn(user);
        when(jwtTokenProvider.gerarToken(user)).thenReturn("token");

        ResponseEntity<?> response = authController.login(dto);

        assertEquals("token", ((com.projeto.demo.dto.AuthResponseDto) response.getBody()).getToken());
    }
}
