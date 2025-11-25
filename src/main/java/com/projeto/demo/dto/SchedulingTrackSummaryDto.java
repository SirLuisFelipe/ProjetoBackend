package com.projeto.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchedulingTrackSummaryDto {

    private Integer trackId;
    private String trackName;
    private Long total;
}
