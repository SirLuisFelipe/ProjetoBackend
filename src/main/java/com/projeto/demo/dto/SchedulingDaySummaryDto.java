package com.projeto.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class SchedulingDaySummaryDto {

    private LocalDate date;
    private List<SchedulingTurnoSummaryDto> turnos;
}
