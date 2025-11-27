package com.projeto.demo.controllers;

import com.projeto.demo.dto.SchedulingDaySummaryDto;
import com.projeto.demo.dto.SchedulingPaymentSummaryDto;
import com.projeto.demo.services.SchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class ReservationAnalyticsControllerTest {

    @Mock
    private SchedulingService schedulingService;

    @InjectMocks
    private ReservationAnalyticsController analyticsController;

    private ReservationAnalyticsController controllerSpy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controllerSpy = Mockito.spy(analyticsController);
    }

    @Test
    void getDailySummary_ShouldReturnServiceResult() {
        SchedulingDaySummaryDto dto = new SchedulingDaySummaryDto(LocalDate.now(), List.of());
        when(schedulingService.getSchedulingSummaryByDay(LocalDate.now())).thenReturn(dto);

        ResponseEntity<?> response = controllerSpy.getDailySummary(LocalDate.now());

        assertEquals(dto, response.getBody());
    }

    @Test
    void getSummaryByPayment_ShouldRequireAdmin() {
        doReturn(true).when(controllerSpy).isAdmin();
        List<SchedulingPaymentSummaryDto> result = List.of(new SchedulingPaymentSummaryDto(1L, "Pix", 2L));
        when(schedulingService.getSchedulingSummaryByPayment(null, null)).thenReturn(result);

        ResponseEntity<?> response = controllerSpy.getSummaryByPayment(null, null);

        assertEquals(result, response.getBody());
    }
}
