package com.projeto.demo.dto;

import com.projeto.demo.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private User user;
    private String token;
}
