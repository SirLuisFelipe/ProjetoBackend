package com.projeto.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSchedulingDto {

    private Long id;

    private Long userId;

    private Long trackId;

    private Long paymentId;

    private LocalDate scheduledDate;   // yyyy-MM-dd

    private String turno;               // MATUTINO / VESPERTINO / NOTURNO

    private String checkinStatus;       // PENDENTE / NAO_REALIZADO / REALIZADO / CANCELADO
}
