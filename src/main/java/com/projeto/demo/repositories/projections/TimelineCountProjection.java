package com.projeto.demo.repositories.projections;

public interface TimelineCountProjection {
    Integer getYear();
    Integer getMonth();
    Long getTotal();
}
