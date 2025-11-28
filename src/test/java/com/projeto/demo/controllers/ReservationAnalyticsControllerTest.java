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
    void getDailySummary_ShouldHandleException() {
        when(schedulingService.getSchedulingSummaryByDay(LocalDate.now()))
                .thenThrow(new RuntimeException("fail"));

        ResponseEntity<?> response = controllerSpy.getDailySummary(LocalDate.now());

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void getSummaryByPayment_ShouldRequireAdmin() {
        doReturn(true).when(controllerSpy).isAdmin();
        List<SchedulingPaymentSummaryDto> result = List.of(new SchedulingPaymentSummaryDto(1L, "Pix", 2L));
        when(schedulingService.getSchedulingSummaryByPayment(null, null)).thenReturn(result);

        ResponseEntity<?> response = controllerSpy.getSummaryByPayment(null, null);

        assertEquals(result, response.getBody());
    }

    @Test
    void getDailySummaryRange_ShouldReturnWhenAdmin() {
        doReturn(true).when(controllerSpy).isAdmin();
        when(schedulingService.getSchedulingSummaryByDayRange(null, null)).thenReturn(List.of());

        ResponseEntity<?> response = controllerSpy.getDailySummaryRange(null, null);

        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getSummaryByTrack_ShouldReturnWhenAdmin() {
        doReturn(true).when(controllerSpy).isAdmin();
        when(schedulingService.getSchedulingSummaryByTrack(null, null)).thenReturn(List.of());

        ResponseEntity<?> response = controllerSpy.getSummaryByTrack(null, null);

        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getSummaryByTrack_ShouldReturnForbiddenWhenNotAdmin() {
        doReturn(false).when(controllerSpy).isAdmin();

        ResponseEntity<?> response = controllerSpy.getSummaryByTrack(null, null);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void getDailySummaryRange_ShouldReturnForbiddenWhenNotAdmin() {
        doReturn(false).when(controllerSpy).isAdmin();

        ResponseEntity<?> response = controllerSpy.getDailySummaryRange(null, null);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void getSummaryByUser_ShouldReturnRanking() {
        doReturn(true).when(controllerSpy).isAdmin();
        when(schedulingService.getTopUsersByScheduling(5, null, null)).thenReturn(List.of());

        ResponseEntity<?> response = controllerSpy.getSummaryByUser(5, null, null);

        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getSummaryByUser_ShouldReturnErrorWhenServiceFails() {
        doReturn(true).when(controllerSpy).isAdmin();
        when(schedulingService.getTopUsersByScheduling(5, null, null))
                .thenThrow(new RuntimeException("error"));

        ResponseEntity<?> response = controllerSpy.getSummaryByUser(5, null, null);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void getTimeline_ShouldReturnPoints() {
        doReturn(true).when(controllerSpy).isAdmin();
        when(schedulingService.getSchedulingTimeline(null, null)).thenReturn(List.of());

        ResponseEntity<?> response = controllerSpy.getTimeline(null, null);

        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getTimeline_ShouldReturnForbiddenWhenNotAdmin() {
        doReturn(false).when(controllerSpy).isAdmin();

        ResponseEntity<?> response = controllerSpy.getTimeline(null, null);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void getCancellationStats_ShouldReturnStats() {
        doReturn(true).when(controllerSpy).isAdmin();
        var stats = new com.projeto.demo.dto.SchedulingCancellationStatsDto(1, 10L, 2L, 20);
        when(schedulingService.getCancellationStats(null, null)).thenReturn(stats);

        ResponseEntity<?> response = controllerSpy.getCancellationStats(null, null);

        assertEquals(stats, response.getBody());
    }

    @Test
    void getCancellationStats_ShouldReturnForbiddenWhenNotAdmin() {
        doReturn(false).when(controllerSpy).isAdmin();

        ResponseEntity<?> response = controllerSpy.getCancellationStats(null, null);

        assertEquals(403, response.getStatusCode().value());
    }
}
