package com.projeto.demo.repositories;

import com.projeto.demo.entities.Scheduling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SchedulingRepository extends JpaRepository<Scheduling, Long> {

    List<Scheduling> findByUserId(Long id);

    List<Scheduling> findByTrackId(Long trackId);

    // Count de Agendamentos por pista, data e turno
    long countByTrack_IdAndScheduledDateAndTurno(Long trackId,
                                                 LocalDate schedulingDate,
                                                 Scheduling.Turno turno);

    // Consultando agendamentos por data e turno
    List<Scheduling> findByScheduledDateAndTurno(LocalDate Scheduling,
                                                  Scheduling.Turno turno);

    List<Scheduling> findByCheckinStatusAndScheduledDateBefore(Scheduling.CheckinStatus status,
                                                               LocalDate date);
}
