package com.projeto.demo.services;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.User;
import com.projeto.demo.entities.Payment;
import com.projeto.demo.entities.Track;
import com.projeto.demo.exceptions.NullIdException;
import com.projeto.demo.exceptions.UnauthorizedActionException;
import com.projeto.demo.repositories.SchedulingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchedulingServiceTest {

    @Mock
    private SchedulingRepository schedulingRepository;

    @Mock
    private UserService userService;

    @Mock
    private PaymentService paymentService;

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
        when(paymentService.findPaymentById(dto.getPaymentId())).thenReturn(payment);
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
        track.setId(10);

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
        track.setId(10);

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
}
