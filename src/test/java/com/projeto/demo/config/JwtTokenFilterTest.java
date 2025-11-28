package com.projeto.demo.config;

import com.projeto.demo.entities.User;
import com.projeto.demo.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class JwtTokenFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        mocks.close();
    }

    @Test
    void doFilterInternal_ShouldSetAuthenticationWhenTokenValid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtTokenProvider.validateToken("token")).thenReturn(true);
        when(jwtTokenProvider.getEmailFromToken("token")).thenReturn("mail@test");
        User user = new User();
        when(userService.findByEmail("mail@test")).thenReturn(user);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldSkipWhenTokenInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtTokenProvider.validateToken("token")).thenReturn(false);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
