package com.projeto.demo.controllers;

import com.projeto.demo.config.JwtTokenProvider;
import com.projeto.demo.dto.AuthResponseDto;
import com.projeto.demo.dto.UserLoginDto;
import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.entities.User;
import com.projeto.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDto userDto) {
        try {
            User user = userService.register(userDto);
            String token = jwtTokenProvider.gerarToken(user);

            return ResponseEntity.ok(new AuthResponseDto(user, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userDto) {
        try {
            User user = userService.authenticate(userDto);
            String token = jwtTokenProvider.gerarToken(user);

            return ResponseEntity.ok(new AuthResponseDto(user, token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
