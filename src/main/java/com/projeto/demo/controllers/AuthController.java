package com.projeto.demo.controllers;

import com.projeto.demo.config.JwtTokenProvider;
import com.projeto.demo.dto.AuthResponseDto;
import com.projeto.demo.dto.UserLoginDto;
import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.entities.User;
import com.projeto.demo.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody UserRegisterDto userDto) {
        try {
            User user = userService.register(userDto);
            String token = jwtTokenProvider.gerarToken(user);

            return ResponseEntity.ok(new AuthResponseDto(user, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody UserLoginDto userDto) {
        try {
            User user = userService.authenticate(userDto);
            String token = jwtTokenProvider.gerarToken(user);

            return ResponseEntity.ok(new AuthResponseDto(user, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
