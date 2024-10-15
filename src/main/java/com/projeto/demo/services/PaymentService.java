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

    public Payment createPayment(String name) {
        Payment payment = new Payment();
        payment.setName(name);

        return paymentRepository.save(payment);
    }

    public List<Payment> listPayments() {
        return paymentRepository.findAll();
    }

    public Payment findPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment method not found"));
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

}
