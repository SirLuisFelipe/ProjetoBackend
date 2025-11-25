package com.projeto.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchedulingUserSummaryDto {

    private Long userId;
    private String userName;
    private Long total;
}
