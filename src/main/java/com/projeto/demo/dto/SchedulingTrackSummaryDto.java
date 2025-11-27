package com.projeto.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchedulingTrackSummaryDto {

    private Long trackId;
    private String trackName;
    private Long total;
}
