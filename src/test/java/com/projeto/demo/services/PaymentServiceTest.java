package com.projeto.demo.services;

import com.projeto.demo.entities.Payment;
import com.projeto.demo.exceptions.PaymentNotFoundException;
import com.projeto.demo.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void createPayment_ShouldSavePayment() {
        Payment payment = new Payment();
        payment.setName("Test Payment");

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.createPayment("Test Payment");
        assertNotNull(result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void findPaymentById_ShouldThrowException_WhenNotFound() {
        Long id = 1L;
        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.findPaymentById(id));
    }
}
