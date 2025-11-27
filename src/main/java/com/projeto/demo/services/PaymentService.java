package com.projeto.demo.services;

import com.projeto.demo.entities.Payment;
import com.projeto.demo.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> listPayments() {
        return paymentRepository.findAll();
    }
}
