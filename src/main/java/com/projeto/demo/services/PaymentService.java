package com.projeto.demo.services;

import com.projeto.demo.models.Payment;
import com.projeto.demo.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Método para buscar um Payment pelo ID
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
}
