package com.projeto.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchedulingTimelinePointDto {

    private String period;
    private Long total;
}
