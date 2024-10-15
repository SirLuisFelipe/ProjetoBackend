package com.projeto.demo.controllers;

import com.projeto.demo.entities.Payment;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.User;
import com.projeto.demo.services.PaymentService;
import com.projeto.demo.services.SchedulingService;
import com.projeto.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduling") // Base da URL para todos os EndPoints
public class SchedulingController {

//    @Autowired
//    private SchedulingService schedulingService;
//
//    @Autowired
//    private UserService userService; // Injetando o serviço de usuários
//
//    @Autowired
//    private PaymentService paymentService; // Injetando o serviço de pagamentos
//
//    // GET para buscar todos os agendamentos
//    @GetMapping
//    public List<Scheduling> getAllScheduling() {
//        return schedulingService.getAllScheduling();
//    }
//
//    // GET para buscar agendamentos por id
//    @GetMapping("/{id}")
//    public ResponseEntity<Scheduling> getSchedulingById(@PathVariable Long id) {
//        Optional<Scheduling> scheduling = schedulingService.getSchedulingById(id);
//        return scheduling.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    // POST para criar um novo agendamento
//    @PostMapping
//    public ResponseEntity<Scheduling> createScheduling(@RequestBody Scheduling scheduling) {
//        // Buscar o usuário pelo ID
//        Optional<User> userOptional = userService.getUserById(scheduling.getUser().getId());
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retorna 404 se o usuário não for encontrado
//        }
//
//        // Buscar o tipo de pagamento pelo ID
//        Optional<Payment> paymentOptional = paymentService.getPaymentById(scheduling.getPayment().getId());
//        if (paymentOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Retorna 400 se o paymentId for inválido
//        }
//
//        // Associar o usuário e o pagamento ao agendamento
//        scheduling.setUser(userOptional.get());
//        scheduling.setPayment(paymentOptional.get());
//
//        // Criar o novo agendamento
//        return ResponseEntity.status(HttpStatus.CREATED).body(schedulingService.createScheduling(scheduling));
//    }
//
//    // PUT para atualizar um agendamento existente
//    @PutMapping("/{id}")
//    public ResponseEntity<Scheduling> updateScheduling(@PathVariable Long id, @RequestBody Scheduling updatedScheduling) {
//        return schedulingService.getSchedulingById(id)
//                .map(existingScheduling -> {
//                    // Atualizando os campos do agendamento existente
//                    existingScheduling.setUser(updatedScheduling.getUser());   // Atualizando o usuário
//                    existingScheduling.setTrackid(updatedScheduling.getTrackid()); // Atualizando o ID da pista
//                    existingScheduling.setDate(updatedScheduling.getDate());   // Atualizando a data
//                    existingScheduling.setStart(updatedScheduling.getStart()); // Atualizando o horário de início
//                    existingScheduling.setEnd(updatedScheduling.getEnd());     // Atualizando o horário de fim
//                    existingScheduling.setPaymentValue(updatedScheduling.getPaymentValue()); // Atualizando o valor do pagamento
//
//                    // Salvando as alterações
//                    schedulingService.updateScheduling(existingScheduling);
//
//                    // Retornando o agendamento atualizado
//                    return ResponseEntity.ok(existingScheduling);
//                })
//                // Caso o agendamento não seja encontrado, retornar 404
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
}
