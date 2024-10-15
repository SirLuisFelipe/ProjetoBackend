package com.projeto.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSchedulingDto {
    private Long id;
    private Long userId;
    private Long trackId;
    private Long paymentId;
    private LocalDateTime scheduledTimeStart;
    private LocalDateTime scheduledTimeEnd;
    private Double paymentValue;
}
