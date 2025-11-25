package com.projeto.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchedulingPaymentSummaryDto {

    private Long paymentId;
    private String paymentName;
    private Long total;
}
