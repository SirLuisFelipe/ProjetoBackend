package com.projeto.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateSchedulingDto {

    private Long id;

    private Long userId;

    private Long trackId;

    private Long paymentId;

    private LocalDate scheduledDate;   // yyyy-MM-dd

    private String turno;               // MATUTINO / VESPERTINO / NOTURNO

    private Double paymentValue;

    // Excluido colunas de data antiga
    // private LocalDateTime scheduledTimeStart;
    // private LocalDateTime scheduledTimeEnd;
}
