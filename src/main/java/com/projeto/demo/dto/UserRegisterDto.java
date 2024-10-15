package com.projeto.demo.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
    private Long id;
    private String cpf;
    private String name;
    private String email;
    private String password;
    private String role;
}
