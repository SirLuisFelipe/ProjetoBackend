package com.projeto.demo.services;

import com.projeto.demo.dto.UserLoginDto;
import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.entities.User;
import com.projeto.demo.exceptions.InvalidCredentialsException;
import com.projeto.demo.exceptions.NullIdException;
import com.projeto.demo.exceptions.UserNotFoundException;
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

    public User register(UserRegisterDto userRegisterDto) {
        User user = mapToEntity(userRegisterDto);
        return userRepository.save(user);
    }
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    public User update(UserRegisterDto userRegisterDto) {
        if (userRegisterDto.getId() == null) {
            throw new NullIdException();
        }

        User user = mapToEntity(userRegisterDto);
        return userRepository.save(user);
    }

    public User mapToEntity(UserRegisterDto userRegisterDto) {
        User user = new User();

        if (userRegisterDto.getId() != null) {
            user = findById(userRegisterDto.getId());
        }

        if (userRegisterDto.getCpf() != null) {
            user.setCpf(userRegisterDto.getCpf());
        }

        if (userRegisterDto.getEmail() != null) {
            user.setEmail(userRegisterDto.getEmail());
        }

        if (userRegisterDto.getName() != null) {
            user.setName(userRegisterDto.getName());
        }

        if (userRegisterDto.getRole() != null) {
            user.setRole(userRegisterDto.getRole());
        }

        if (userRegisterDto.getPassword() != null) {
            user.setEncodedPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        }

        return user;
    }

    public User authenticate(UserLoginDto dto) {
        User user = findByEmail(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getEncodedPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findByCpf(String cpf) {
        return userRepository.findByCpf(cpf).orElseThrow(UserNotFoundException::new);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
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