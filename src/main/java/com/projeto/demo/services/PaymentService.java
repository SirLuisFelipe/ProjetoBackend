package com.projeto.demo.services;

import com.projeto.demo.entities.Payment;
import com.projeto.demo.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> listPayments() {
        return paymentRepository.findAll();
    }
}
