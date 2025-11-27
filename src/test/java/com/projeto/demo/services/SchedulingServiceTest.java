package com.projeto.demo.services;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.dto.SchedulingCancellationStatsDto;
import com.projeto.demo.dto.SchedulingDaySummaryDto;
import com.projeto.demo.dto.SchedulingTimelinePointDto;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.User;
import com.projeto.demo.entities.Payment;
import com.projeto.demo.entities.Track;
import com.projeto.demo.exceptions.NullIdException;
import com.projeto.demo.exceptions.SchedulingNotFoundException;
import com.projeto.demo.exceptions.UnauthorizedActionException;
import com.projeto.demo.repositories.PaymentRepository;
import com.projeto.demo.repositories.SchedulingRepository;
import com.projeto.demo.repositories.projections.DateTurnoCountProjection;
import com.projeto.demo.repositories.projections.PaymentCountProjection;
import com.projeto.demo.repositories.projections.TimelineCountProjection;
import com.projeto.demo.repositories.projections.TrackCountProjection;
import com.projeto.demo.repositories.projections.TurnoCountProjection;
import com.projeto.demo.repositories.projections.UserCountProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SchedulingServiceTest {

    @Mock
    private SchedulingRepository schedulingRepository;

    @Mock
    private UserService userService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TrackService trackService;

    @InjectMocks
    private SchedulingService schedulingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createScheduling_ShouldSaveScheduling() {
        CreateSchedulingDto dto = new CreateSchedulingDto();
        dto.setUserId(1L);
        dto.setPaymentId(1L);
        dto.setTrackId(1L);
        dto.setScheduledDate(LocalDate.now().plusDays(1));
        dto.setTurno("MATUTINO");

        User user = new User();
        Payment payment = new Payment();
        Track track = new Track();

        when(userService.findById(dto.getUserId())).thenReturn(user);
        when(paymentRepository.findById(dto.getPaymentId())).thenReturn(Optional.of(payment));
        when(trackService.findTrackById(dto.getTrackId())).thenReturn(track);

        Scheduling scheduling = new Scheduling();
        when(schedulingRepository.save(any(Scheduling.class))).thenReturn(scheduling);

        Scheduling result = schedulingService.createScheduling(dto);
        assertNotNull(result);
        verify(schedulingRepository, times(1)).save(any(Scheduling.class));
    }

    @Test
    void updateScheduling_ShouldThrowException_WhenIdIsNull() {
        CreateSchedulingDto dto = new CreateSchedulingDto();
        dto.setId(null);

        assertThrows(NullIdException.class, () -> schedulingService.updateScheduling(dto, null));
    }

    @Test
    void createScheduling_ShouldThrowException_WhenDateInPast() {
        CreateSchedulingDto dto = new CreateSchedulingDto();
        dto.setUserId(1L);
        dto.setTrackId(1L);
        dto.setTurno("MATUTINO");
        dto.setScheduledDate(LocalDate.now().minusDays(1));

        assertThrows(IllegalStateException.class, () -> schedulingService.createScheduling(dto));
    }

    @Test
    void deleteScheduling_ShouldThrowException_WhenDateInPast() {
        Scheduling scheduling = new Scheduling();
        scheduling.setId(1L);
        scheduling.setScheduledDate(LocalDate.now().minusDays(1));
        scheduling.setUser(new User());

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));

        assertThrows(IllegalStateException.class, () -> schedulingService.deleteScheduling(1L));
        verify(schedulingRepository, never()).delete(any());
    }

    @Test
    void findSchedulingById_ShouldReturnScheduling() {
        Scheduling scheduling = new Scheduling();
        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));

        Scheduling result = schedulingService.findSchedulingById(1L);

        assertEquals(scheduling, result);
    }

    @Test
    void findSchedulingById_ShouldThrowException_WhenNotFound() {
        when(schedulingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SchedulingNotFoundException.class, () -> schedulingService.findSchedulingById(1L));
    }

    @Test
    void listSchedulings_ShouldReturnAll() {
        Scheduling scheduling = new Scheduling();
        when(schedulingRepository.findAll()).thenReturn(List.of(scheduling));

        assertEquals(1, schedulingService.listSchedulings().size());
        verify(schedulingRepository).findAll();
    }

    @Test
    void listSchedulingsByUserId_ShouldReturnUserSchedules() {
        Scheduling scheduling = new Scheduling();
        when(schedulingRepository.findByUserId(5L)).thenReturn(List.of(scheduling));

        assertEquals(1, schedulingService.listSchedulingsByUserId(5L).size());
        verify(schedulingRepository).findByUserId(5L);
    }

    @Test
    void checkAvailability_ShouldReturnTrueWhenCapacityNotReached() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        when(schedulingRepository.countByScheduledDateAndTurno(
                date, Scheduling.Turno.MATUTINO)).thenReturn(10L);

        assertTrue(schedulingService.checkAvailability(2L, date, Scheduling.Turno.MATUTINO));
    }

    @Test
    void checkAvailability_ShouldReturnFalseWhenCapacityReached() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        when(schedulingRepository.countByScheduledDateAndTurno(
                date, Scheduling.Turno.MATUTINO)).thenReturn(25L);

        assertFalse(schedulingService.checkAvailability(2L, date, Scheduling.Turno.MATUTINO));
    }

    @Test
    void updateCheckinStatus_ShouldAllowUserRealizadoOnSameDay() {
        User user = new User();
        user.setId(1L);
        user.setRole("USER");

        Scheduling scheduling = new Scheduling();
        scheduling.setId(1L);
        scheduling.setScheduledDate(LocalDate.now());
        scheduling.setUser(user);

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));
        when(schedulingRepository.save(any(Scheduling.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scheduling result = schedulingService.updateCheckinStatus(1L, "REALIZADO", user);

        assertEquals(Scheduling.CheckinStatus.REALIZADO, result.getCheckinStatus());
        verify(schedulingRepository).save(scheduling);
    }

    @Test
    void updateCheckinStatus_ShouldAllowAdminCancel() {
        User owner = new User();
        owner.setId(2L);
        owner.setRole("USER");

        User admin = new User();
        admin.setId(99L);
        admin.setRole("ADMIN");

        Scheduling scheduling = new Scheduling();
        scheduling.setId(1L);
        scheduling.setScheduledDate(LocalDate.now());
        scheduling.setUser(owner);

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));
        when(schedulingRepository.save(any(Scheduling.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scheduling result = schedulingService.updateCheckinStatus(1L, "CANCELADO", admin);

        assertEquals(Scheduling.CheckinStatus.CANCELADO, result.getCheckinStatus());
        verify(schedulingRepository).save(scheduling);
    }

    @Test
    void updateCheckinStatus_ShouldAllowAdminNaoRealizado() {
        User owner = new User();
        owner.setId(2L);
        owner.setRole("USER");

        User admin = new User();
        admin.setId(99L);
        admin.setRole("ADMIN");

        Scheduling scheduling = new Scheduling();
        scheduling.setId(1L);
        scheduling.setScheduledDate(LocalDate.now());
        scheduling.setUser(owner);

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));
        when(schedulingRepository.save(any(Scheduling.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scheduling result = schedulingService.updateCheckinStatus(1L, "Nao realizado", admin);

        assertEquals(Scheduling.CheckinStatus.NAO_REALIZADO, result.getCheckinStatus());
        verify(schedulingRepository).save(scheduling);
    }

    @Test
    void updateCheckinStatus_ShouldBlockUserCancelOnPastDate() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole("USER");

        Scheduling scheduling = new Scheduling();
        scheduling.setId(1L);
        scheduling.setScheduledDate(LocalDate.now().minusDays(1));
        scheduling.setUser(owner);

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));

        assertThrows(IllegalStateException.class,
                () -> schedulingService.updateCheckinStatus(1L, "CANCELADO", owner));
        verify(schedulingRepository, never()).save(any());
    }

    @Test
    void updateCheckinStatus_ShouldBlockUserNaoRealizado() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole("USER");

        Scheduling scheduling = new Scheduling();
        scheduling.setId(1L);
        scheduling.setScheduledDate(LocalDate.now());
        scheduling.setUser(owner);

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));

        assertThrows(IllegalStateException.class,
                () -> schedulingService.updateCheckinStatus(1L, "NAO_REALIZADO", owner));
        verify(schedulingRepository, never()).save(any());
    }

    @Test
    void updateCheckinStatus_ShouldAllowUserCancelOnSameDay() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole("USER");

        Scheduling scheduling = new Scheduling();
        scheduling.setId(1L);
        scheduling.setScheduledDate(LocalDate.now());
        scheduling.setUser(owner);

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(scheduling));
        when(schedulingRepository.save(any(Scheduling.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scheduling result = schedulingService.updateCheckinStatus(1L, "CANCELADO", owner);

        assertEquals(Scheduling.CheckinStatus.CANCELADO, result.getCheckinStatus());
        verify(schedulingRepository).save(scheduling);
    }

    @Test
    void markPastPendingsAsNaoRealizado_ShouldUpdateStatuses() {
        Scheduling past = new Scheduling();
        past.setId(1L);
        past.setScheduledDate(LocalDate.now().minusDays(1));
        past.setCheckinStatus(Scheduling.CheckinStatus.PENDENTE);

        List<Scheduling> pendings = List.of(past);
        when(schedulingRepository.findByCheckinStatusAndScheduledDateBefore(
                eq(Scheduling.CheckinStatus.PENDENTE), any(LocalDate.class)))
                .thenReturn(pendings);

        schedulingService.markPastPendingsAsNaoRealizado();

        assertEquals(Scheduling.CheckinStatus.NAO_REALIZADO, past.getCheckinStatus());
        verify(schedulingRepository).saveAll(pendings);
    }

    @Test
    void updateScheduling_ShouldApplyCheckinStatusChangeWhenProvided() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole("USER");

        Track track = new Track();
        track.setId(10L);

        Scheduling existing = new Scheduling();
        existing.setId(1L);
        existing.setUser(owner);
        existing.setTrack(track);
        existing.setScheduledDate(LocalDate.now());
        existing.setTurno(Scheduling.Turno.MATUTINO);
        existing.setCheckinStatus(Scheduling.CheckinStatus.PENDENTE);

        CreateSchedulingDto dto = new CreateSchedulingDto();
        dto.setId(1L);
        dto.setUserId(owner.getId());
        dto.setTrackId(track.getId().longValue());
        dto.setScheduledDate(LocalDate.now());
        dto.setTurno("MATUTINO");
        dto.setCheckinStatus("REALIZADO");

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userService.findById(owner.getId())).thenReturn(owner);
        when(trackService.findTrackById(track.getId().longValue())).thenReturn(track);
        when(schedulingRepository.save(any(Scheduling.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scheduling result = schedulingService.updateScheduling(dto, owner);

        assertEquals(Scheduling.CheckinStatus.REALIZADO, result.getCheckinStatus());
        verify(schedulingRepository).save(existing);
    }

    @Test
    void updateScheduling_ShouldRequireActorWhenChangingCheckinStatus() {
        User owner = new User();
        owner.setId(1L);
        owner.setRole("USER");

        Track track = new Track();
        track.setId(10L);

        Scheduling existing = new Scheduling();
        existing.setId(1L);
        existing.setUser(owner);
        existing.setTrack(track);
        existing.setScheduledDate(LocalDate.now());
        existing.setTurno(Scheduling.Turno.MATUTINO);

        CreateSchedulingDto dto = new CreateSchedulingDto();
        dto.setId(1L);
        dto.setUserId(owner.getId());
        dto.setTrackId(track.getId().longValue());
        dto.setScheduledDate(LocalDate.now());
        dto.setTurno("MATUTINO");
        dto.setCheckinStatus("REALIZADO");

        when(schedulingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userService.findById(owner.getId())).thenReturn(owner);
        when(trackService.findTrackById(track.getId().longValue())).thenReturn(track);

        assertThrows(UnauthorizedActionException.class,
                () -> schedulingService.updateScheduling(dto, null));
        verify(schedulingRepository, never()).save(any());
    }

    @Test
    void getSchedulingSummaryByDay_ShouldReturnAllTurnos() {
        LocalDate date = LocalDate.now();

        TurnoCountProjection morning = new SimpleTurnoCountProjection(Scheduling.Turno.MATUTINO, 5L);
        TurnoCountProjection night = new SimpleTurnoCountProjection(Scheduling.Turno.NOTURNO, 2L);

        when(schedulingRepository.countByDateGroupedByTurno(date))
                .thenReturn(List.of(morning, night));

        SchedulingDaySummaryDto summary = schedulingService.getSchedulingSummaryByDay(date);

        assertEquals(date, summary.getDate());
        assertEquals(3, summary.getTurnos().size());
        assertEquals(5L, summary.getTurnos().stream()
                .filter(t -> t.getTurno() == Scheduling.Turno.MATUTINO)
                .findFirst().orElseThrow().getTotal());
        assertEquals(0L, summary.getTurnos().stream()
                .filter(t -> t.getTurno() == Scheduling.Turno.VESPERTINO)
                .findFirst().orElseThrow().getTotal());
    }

    @Test
    void getSchedulingSummaryByDay_ShouldThrowWhenDateNull() {
        assertThrows(IllegalArgumentException.class,
                () -> schedulingService.getSchedulingSummaryByDay(null));
    }

    @Test
    void getSchedulingSummaryByTrack_ShouldMapResults() {
        TrackCountProjection projection = new SimpleTrackCountProjection(1L, "Street", 10L);
        when(schedulingRepository.countByTrack()).thenReturn(List.of(projection));

        var result = schedulingService.getSchedulingSummaryByTrack(null, null);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getTotal());
        verify(schedulingRepository).countByTrack();
    }

    @Test
    void getSchedulingSummaryByTrack_ShouldUseBetweenWhenDatesProvided() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 10);
        when(schedulingRepository.countByTrackBetween(eq(start), eq(end))).thenReturn(List.of());

        schedulingService.getSchedulingSummaryByTrack(start, end);

        verify(schedulingRepository).countByTrackBetween(eq(start), eq(end));
    }

    @Test
    void getSchedulingSummaryByPayment_ShouldSupportNulls() {
        PaymentCountProjection projection = new SimplePaymentCountProjection(null, null, 3L);
        when(schedulingRepository.countByPayment()).thenReturn(List.of(projection));

        var result = schedulingService.getSchedulingSummaryByPayment(null, null);

        assertEquals(1, result.size());
        assertNull(result.get(0).getPaymentId());
        verify(schedulingRepository).countByPayment();
    }

    @Test
    void getSchedulingSummaryByPayment_ShouldUseBetweenWhenDatesProvided() {
        LocalDate start = LocalDate.of(2025, 3, 1);
        LocalDate end = LocalDate.of(2025, 3, 31);
        when(schedulingRepository.countByPaymentBetween(eq(start), eq(end))).thenReturn(List.of());

        schedulingService.getSchedulingSummaryByPayment(start, end);

        verify(schedulingRepository).countByPaymentBetween(eq(start), eq(end));
    }

    @Test
    void getTopUsersByScheduling_ShouldDefaultLimit() {
        UserCountProjection projection = new SimpleUserCountProjection(1L, "User", 4L);
        when(schedulingRepository.countByUser(any(Pageable.class))).thenReturn(List.of(projection));

        var result = schedulingService.getTopUsersByScheduling(0, null, null);

        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getTotal());
        verify(schedulingRepository).countByUser(any(Pageable.class));
    }

    @Test
    void getTopUsersByScheduling_ShouldUseBetweenWhenDatesProvided() {
        LocalDate start = LocalDate.of(2025, 2, 1);
        LocalDate end = LocalDate.of(2025, 2, 28);
        when(schedulingRepository.countByUserBetween(eq(start), eq(end), any(Pageable.class))).thenReturn(List.of());

        schedulingService.getTopUsersByScheduling(5, start, end);

        verify(schedulingRepository).countByUserBetween(eq(start), eq(end), any(Pageable.class));
    }

    @Test
    void getSchedulingTimeline_ShouldFormatPeriod() {
        TimelineCountProjection projection = new SimpleTimelineCountProjection(2025, 1, 7L);
        LocalDate minDate = LocalDate.of(2025, 1, 1);
        LocalDate maxDate = LocalDate.of(2025, 2, 28);
        when(schedulingRepository.findMinimumScheduledDate()).thenReturn(minDate);
        when(schedulingRepository.findMaximumScheduledDate()).thenReturn(maxDate);
        when(schedulingRepository.countTimelineBetween(eq(minDate), eq(maxDate))).thenReturn(List.of(projection));

        List<SchedulingTimelinePointDto> result = schedulingService.getSchedulingTimeline(null, null);

        assertEquals("2025-01", result.get(0).getPeriod());
        verify(schedulingRepository).countTimelineBetween(eq(minDate), eq(maxDate));
    }

    @Test
    void getSchedulingTimeline_ShouldUseProvidedRange() {
        LocalDate start = LocalDate.of(2025, 5, 1);
        LocalDate end = LocalDate.of(2025, 5, 31);
        when(schedulingRepository.countTimelineBetween(eq(start), eq(end))).thenReturn(List.of());

        schedulingService.getSchedulingTimeline(start, end);

        verify(schedulingRepository).countTimelineBetween(eq(start), eq(end));
    }

    @Test
    void getCancellationStats_ShouldCalculatePercentage() {
        LocalDate minDate = LocalDate.of(2025, 1, 1);
        LocalDate maxDate = LocalDate.of(2025, 1, 31);
        when(schedulingRepository.findMinimumScheduledDate()).thenReturn(minDate);
        when(schedulingRepository.findMaximumScheduledDate()).thenReturn(maxDate);
        when(schedulingRepository.countByScheduledDateBetween(eq(minDate), eq(maxDate))).thenReturn(20L);
        when(schedulingRepository.countByScheduledDateBetweenAndCheckinStatus(eq(minDate), eq(maxDate), any()))
                .thenReturn(5L);

        SchedulingCancellationStatsDto stats = schedulingService.getCancellationStats(null, null);

        assertEquals(25.0, stats.getPercentage());
        assertEquals(5L, stats.getCancelled());
        verify(schedulingRepository).countByScheduledDateBetween(eq(minDate), eq(maxDate));
    }

    @Test
    void getCancellationStats_ShouldUseProvidedRange() {
        LocalDate start = LocalDate.of(2025, 4, 1);
        LocalDate end = LocalDate.of(2025, 4, 30);
        when(schedulingRepository.countByScheduledDateBetween(eq(start), eq(end))).thenReturn(10L);
        when(schedulingRepository.countByScheduledDateBetweenAndCheckinStatus(eq(start), eq(end), any()))
                .thenReturn(2L);

        SchedulingCancellationStatsDto stats = schedulingService.getCancellationStats(start, end);

        assertEquals(20.0, stats.getPercentage());
        verify(schedulingRepository).countByScheduledDateBetween(eq(start), eq(end));
    }

    @Test
    void getSchedulingSummaryByDayRange_ShouldBuildForEachDay() {
        DateTurnoCountProjection projection = new SimpleDateTurnoCountProjection(LocalDate.now(),
                Scheduling.Turno.MATUTINO, 2L);
        when(schedulingRepository.countByDateRangeGroupedByTurno(any(), any()))
                .thenReturn(List.of(projection));

        var result = schedulingService.getSchedulingSummaryByDayRange(LocalDate.now(), LocalDate.now());

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getTurnos().size());
    }

    @Test
    void getSchedulingSummaryByDayRange_ShouldUseAllDataWhenNoDates() {
        LocalDate first = LocalDate.of(2025, 1, 1);
        LocalDate second = LocalDate.of(2025, 1, 2);
        DateTurnoCountProjection p1 = new SimpleDateTurnoCountProjection(first, Scheduling.Turno.MATUTINO, 1L);
        DateTurnoCountProjection p2 = new SimpleDateTurnoCountProjection(second, Scheduling.Turno.MATUTINO, 2L);
        when(schedulingRepository.findMinimumScheduledDate()).thenReturn(first);
        when(schedulingRepository.findMaximumScheduledDate()).thenReturn(second);
        when(schedulingRepository.countByDateRangeGroupedByTurno(eq(first), eq(second)))
                .thenReturn(List.of(p1, p2));

        var result = schedulingService.getSchedulingSummaryByDayRange(null, null);

        assertEquals(2, result.size());
        verify(schedulingRepository).countByDateRangeGroupedByTurno(eq(first), eq(second));
    }

    @Test
    void getSchedulingSummaryByDayRange_ShouldReturnEmptyWhenNoData() {
        when(schedulingRepository.findMinimumScheduledDate()).thenReturn(null);
        when(schedulingRepository.findMaximumScheduledDate()).thenReturn(null);

        var result = schedulingService.getSchedulingSummaryByDayRange(null, null);

        assertTrue(result.isEmpty());
        verify(schedulingRepository, never()).countByDateRangeGroupedByTurno(any(), any());
    }

    @Test
    void getSchedulingSummaryByTrack_ShouldRequireBothDates() {
        assertThrows(IllegalArgumentException.class,
                () -> schedulingService.getSchedulingSummaryByTrack(LocalDate.now(), null));
    }

    private static class SimpleTurnoCountProjection implements TurnoCountProjection {
        private final Scheduling.Turno turno;
        private final Long total;

        private SimpleTurnoCountProjection(Scheduling.Turno turno, Long total) {
            this.turno = turno;
            this.total = total;
        }

        @Override
        public Scheduling.Turno getTurno() {
            return turno;
        }

        @Override
        public Long getTotal() {
            return total;
        }
    }

    private static class SimpleTrackCountProjection implements TrackCountProjection {
        private final Long trackId;
        private final String trackName;
        private final Long total;

        private SimpleTrackCountProjection(Long trackId, String trackName, Long total) {
            this.trackId = trackId;
            this.trackName = trackName;
            this.total = total;
        }

        @Override
        public Long getTrackId() {
            return trackId;
        }

        @Override
        public String getTrackName() {
            return trackName;
        }

        @Override
        public Long getTotal() {
            return total;
        }
    }

    private static class SimplePaymentCountProjection implements PaymentCountProjection {
        private final Long paymentId;
        private final String paymentName;
        private final Long total;

        private SimplePaymentCountProjection(Long paymentId, String paymentName, Long total) {
            this.paymentId = paymentId;
            this.paymentName = paymentName;
            this.total = total;
        }

        @Override
        public Long getPaymentId() {
            return paymentId;
        }

        @Override
        public String getPaymentName() {
            return paymentName;
        }

        @Override
        public Long getTotal() {
            return total;
        }
    }

    private static class SimpleUserCountProjection implements UserCountProjection {
        private final Long userId;
        private final String userName;
        private final Long total;

        private SimpleUserCountProjection(Long userId, String userName, Long total) {
            this.userId = userId;
            this.userName = userName;
            this.total = total;
        }

        @Override
        public Long getUserId() {
            return userId;
        }

        @Override
        public String getUserName() {
            return userName;
        }

        @Override
        public Long getTotal() {
            return total;
        }
    }

    private static class SimpleTimelineCountProjection implements TimelineCountProjection {
        private final Integer year;
        private final Integer month;
        private final Long total;

        private SimpleTimelineCountProjection(Integer year, Integer month, Long total) {
            this.year = year;
            this.month = month;
            this.total = total;
        }

        @Override
        public Integer getYear() {
            return year;
        }

        @Override
        public Integer getMonth() {
            return month;
        }

        @Override
        public Long getTotal() {
            return total;
        }
    }

    private static class SimpleDateTurnoCountProjection implements DateTurnoCountProjection {
        private final LocalDate date;
        private final Scheduling.Turno turno;
        private final Long total;

        private SimpleDateTurnoCountProjection(LocalDate date, Scheduling.Turno turno, Long total) {
            this.date = date;
            this.turno = turno;
            this.total = total;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        @Override
        public Scheduling.Turno getTurno() {
            return turno;
        }

        @Override
        public Long getTotal() {
            return total;
        }
    }
}
