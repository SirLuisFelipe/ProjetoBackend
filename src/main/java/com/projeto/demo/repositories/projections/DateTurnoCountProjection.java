package com.projeto.demo.repositories.projections;

import com.projeto.demo.entities.Scheduling;

import java.time.LocalDate;

public interface DateTurnoCountProjection {
    LocalDate getDate();
    Scheduling.Turno getTurno();
    Long getTotal();
}
