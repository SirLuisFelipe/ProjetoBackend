package com.projeto.demo.controllers;

import com.projeto.demo.exceptions.UnauthorizedActionException;
import com.projeto.demo.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController extends BaseController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/")
    public ResponseEntity<?> createPayment(@RequestBody String paymentName) {
        try {
            if (!isAdmin()) {
                throw new UnauthorizedActionException();
            }

            return ResponseEntity.ok(paymentService.createPayment(paymentName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> listPayments() {
        try {
            return ResponseEntity.ok(paymentService.listPayments());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findPaymentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.findPaymentById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        try {
            if (!isAdmin()) {
                throw new UnauthorizedActionException();
            }

            paymentService.deletePayment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
