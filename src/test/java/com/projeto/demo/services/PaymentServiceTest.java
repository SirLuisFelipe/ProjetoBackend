package com.projeto.demo.services;

import com.projeto.demo.entities.Payment;
import com.projeto.demo.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listPayments_ShouldReturnAll() {
        when(paymentRepository.findAll()).thenReturn(List.of(new Payment()));

        assertEquals(1, paymentService.listPayments().size());
        verify(paymentRepository).findAll();
    }
}
