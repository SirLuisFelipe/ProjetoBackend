package com.projeto.demo.repositories;

import com.projeto.demo.entities.Scheduling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulingRepository extends JpaRepository<Scheduling, Long> {

    List<Scheduling> findByUserId(Long id);
    List<Scheduling> findByTrackId(Long trackId);

}