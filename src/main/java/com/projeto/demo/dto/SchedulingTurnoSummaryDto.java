package com.projeto.demo.dto;

import com.projeto.demo.entities.Scheduling;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchedulingTurnoSummaryDto {

    private Scheduling.Turno turno;
    private Long total;
}
