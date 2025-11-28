package com.projeto.demo.controllers;

import com.projeto.demo.entities.Payment;
import com.projeto.demo.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listPayments_ShouldReturnAll() {
        List<Payment> payments = List.of(new Payment());
        when(paymentService.listPayments()).thenReturn(payments);

        ResponseEntity<List<Payment>> response = paymentController.listPayments();

        assertEquals(payments, response.getBody());
        verify(paymentService).listPayments();
    }
}
