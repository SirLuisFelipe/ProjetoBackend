package com.projeto.demo.repositories.projections;

import com.projeto.demo.entities.Scheduling;

public interface TurnoCountProjection {
    Scheduling.Turno getTurno();
    Long getTotal();
}
