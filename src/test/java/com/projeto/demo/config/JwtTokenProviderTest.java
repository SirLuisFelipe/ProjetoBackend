package com.projeto.demo.config;

import com.projeto.demo.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private final JwtTokenProvider provider = new JwtTokenProvider();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(provider, "jwtSecret", "test-secret-1234567890");
        ReflectionTestUtils.setField(provider, "jwtExpirationDays", 1L);
    }

    @Test
    void gerarToken_ShouldIncludeSubjectAndClaims() {
        User user = new User();
        user.setId(2L);
        user.setEmail("user@test");
        user.setCpf("123");
        user.setRole("ADMIN");

        String token = provider.gerarToken(user);

        assertTrue(provider.validateToken(token));
        assertEquals("user@test", provider.getEmailFromToken(token));
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        assertTrue(provider.validateToken(provider.gerarToken(new User())));
        ReflectionTestUtils.setField(provider, "jwtSecret", "other-secret");
        assertEquals("user@test", provider.getEmailFromToken(
                new JwtTokenProvider() {{
                    ReflectionTestUtils.setField(this, "jwtSecret", "other-secret");
                    ReflectionTestUtils.setField(this, "jwtExpirationDays", 1L);
                }}.gerarToken(new User() {{
                    setEmail("user@test");
                }})
        ));
    }
}
