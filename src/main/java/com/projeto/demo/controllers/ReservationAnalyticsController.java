package com.projeto.demo.controllers;

import com.projeto.demo.dto.SchedulingDaySummaryDto;
import com.projeto.demo.exceptions.UnauthorizedActionException;
import com.projeto.demo.services.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/scheduling/analytics")
public class ReservationAnalyticsController extends BaseController {

    @Autowired
    private final SchedulingService schedulingService;

    public ReservationAnalyticsController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @GetMapping("/day")
    public ResponseEntity<SchedulingDaySummaryDto> getDailySummary(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            SchedulingDaySummaryDto summary = schedulingService.getSchedulingSummaryByDay(date);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/day-range")
    public ResponseEntity<java.util.List<com.projeto.demo.dto.SchedulingDaySummaryDto>> getDailySummaryRange(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ensureAdminUser();
            return ResponseEntity.ok(schedulingService.getSchedulingSummaryByDayRange(startDate, endDate));
        } catch (UnauthorizedActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-track")
    public ResponseEntity<java.util.List<com.projeto.demo.dto.SchedulingTrackSummaryDto>> getSummaryByTrack(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ensureAdminUser();
            return ResponseEntity.ok(schedulingService.getSchedulingSummaryByTrack(startDate, endDate));
        } catch (UnauthorizedActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-payment")
    public ResponseEntity<java.util.List<com.projeto.demo.dto.SchedulingPaymentSummaryDto>> getSummaryByPayment(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ensureAdminUser();
            return ResponseEntity.ok(schedulingService.getSchedulingSummaryByPayment(startDate, endDate));
        } catch (UnauthorizedActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-user")
    public ResponseEntity<java.util.List<com.projeto.demo.dto.SchedulingUserSummaryDto>> getSummaryByUser(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ensureAdminUser();
            return ResponseEntity.ok(schedulingService.getTopUsersByScheduling(limit, startDate, endDate));
        } catch (UnauthorizedActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/timeline")
    public ResponseEntity<java.util.List<com.projeto.demo.dto.SchedulingTimelinePointDto>> getTimeline(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ensureAdminUser();
            return ResponseEntity.ok(schedulingService.getSchedulingTimeline(startDate, endDate));
        } catch (UnauthorizedActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cancellations")
    public ResponseEntity<com.projeto.demo.dto.SchedulingCancellationStatsDto> getCancellationStats(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            ensureAdminUser();
            return ResponseEntity.ok(schedulingService.getCancellationStats(startDate, endDate));
        } catch (UnauthorizedActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void ensureAdminUser() {
        if (!isAdmin()) {
            throw new UnauthorizedActionException("Acesso permitido apenas para administradores.");
        }
    }
}
