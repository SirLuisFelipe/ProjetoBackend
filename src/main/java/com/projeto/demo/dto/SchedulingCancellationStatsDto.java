package com.projeto.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchedulingCancellationStatsDto {

    private int months;
    private Long total;
    private Long cancelled;
    private double percentage;
}
