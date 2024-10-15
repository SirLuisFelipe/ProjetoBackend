package com.projeto.demo.services;

import com.projeto.demo.dto.UserLoginDto;
import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.entities.User;
import com.projeto.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserRegisterDto userDto) {
        User user = new User();
        user.setCpf(userDto.getCpf());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setRole(userDto.getRole());
        user.setEncodedPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(UserLoginDto dto) {
        User user = findByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getEncodedPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        return user;
    }

    public User update(UserRegisterDto userDto) {
        if (userDto.getId() == null) {
            throw new RuntimeException("Id não pode ser nulo");
        }

        User user = findById(userDto.getId());

        user.setCpf(userDto.getCpf());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setRole(userDto.getRole());
        user.setEncodedPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID:" + id));
    }

    public User findByCpf(String cpf) {
        return userRepository.findByCpf(cpf).orElseThrow(() -> new RuntimeException("Usuário não encontrado com o CPF:" + cpf));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado com o email:" + email));
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public void deleteByCpf(String cpf) {
        User user = findByCpf(cpf);
        userRepository.delete(user);
    }

    public void deleteById(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }


}