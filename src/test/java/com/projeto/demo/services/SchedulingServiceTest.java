package com.projeto.demo.services;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.User;
import com.projeto.demo.entities.Payment;
import com.projeto.demo.entities.Track;
import com.projeto.demo.exceptions.NullIdException;
import com.projeto.demo.repositories.SchedulingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

        assertThrows(NullIdException.class, () -> schedulingService.updateScheduling(dto));
    }
}
