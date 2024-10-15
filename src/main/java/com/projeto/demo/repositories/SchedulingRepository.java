package com.projeto.demo.repositories;

import com.projeto.demo.entities.Scheduling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulingRepository extends JpaRepository<Scheduling, Long> {

    // buscar todos os agendamentos de um usuário específico
    List<Scheduling> findByUserId(Long id);


    // Busca todos os agendamentos de um tipo de pista específico
    List<Scheduling> findByTrackId(Integer trackid);
}