package com.projeto.demo.controllers;

import com.projeto.demo.entities.User;
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
            User user = getLoggedUser();

            if (!user.getRole().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have permission to create a payment");
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
            paymentService.deletePayment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
