package com.projeto.demo.services;

import com.projeto.demo.dto.UserLoginDto;
import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.entities.User;
import com.projeto.demo.exceptions.InvalidCredentialsException;
import com.projeto.demo.exceptions.NullIdException;
import com.projeto.demo.exceptions.UserNotFoundException;
import com.projeto.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldSaveUser() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");

        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(dto);
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenIdIsNull() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setId(null);

        assertThrows(NullIdException.class, () -> userService.update(dto));
    }

    @Test
    void authenticate_ShouldThrowException_WhenInvalidPassword() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("wrongpassword");

        User user = new User();
        user.setEncodedPassword("encodedPassword");

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getEncodedPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.authenticate(loginDto));
    }

    @Test
    void findById_ShouldReturnUser_WhenFound() {
        Long id = 1L;
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.findById(id);
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void searchUsersByName_ShouldDelegateToRepository() {
        when(userRepository.findByNameContainingIgnoreCase("ana"))
                .thenReturn(List.of(new User()));

        assertEquals(1, userService.searchUsersByName("ana").size());
        verify(userRepository).findByNameContainingIgnoreCase("ana");
    }

    @Test
    void listUsers_ShouldReturnAll() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        assertEquals(1, userService.listUsers().size());
        verify(userRepository).findAll();
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        when(userRepository.findByEmail("test")).thenReturn(Optional.of(user));

        assertEquals(user, userService.findByEmail("test"));
    }

    @Test
    void findByEmail_ShouldThrow_WhenNotFound() {
        when(userRepository.findByEmail("test")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findByEmail("test"));
    }

    @Test
    void deleteById_ShouldRemoveExistingUser() {
        User user = new User();
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        userService.deleteById(5L);

        verify(userRepository).delete(user);
    }
}
