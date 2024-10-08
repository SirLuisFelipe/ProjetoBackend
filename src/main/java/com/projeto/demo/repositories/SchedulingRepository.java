package com.projeto.demo.repositories;

import com.projeto.demo.models.Scheduling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SchedulingRepository extends JpaRepository<Scheduling, Long> {

    // buscar todos os agendamentos de um usuário específico
    List<Scheduling> findByUser_Id(Long id);


    // Busca todos os agendamentos de um tipo de pista específico
    List<Scheduling> findByTrackid(Integer trackid);
}