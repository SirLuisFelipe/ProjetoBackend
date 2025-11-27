package com.projeto.demo.controllers;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.User;
import com.projeto.demo.services.SchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SchedulingControllerTest {

    @Mock
    private SchedulingService schedulingService;

    @InjectMocks
    private SchedulingController schedulingController;

    private SchedulingController controllerSpy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controllerSpy = Mockito.spy(schedulingController);
    }

    @Test
    void createScheduling_ShouldInvokeService() {
        CreateSchedulingDto dto = new CreateSchedulingDto();
        Scheduling scheduling = new Scheduling();
        when(schedulingService.createScheduling(dto)).thenReturn(scheduling);

        ResponseEntity<?> response = controllerSpy.createScheduling(dto);

        assertEquals(scheduling, response.getBody());
        verify(schedulingService).createScheduling(dto);
    }

    @Test
    void updateScheduling_ShouldUseLoggedUser() {
        CreateSchedulingDto dto = new CreateSchedulingDto();
        dto.setId(1L);
        User user = new User();

        doReturn(user).when(controllerSpy).getLoggedUser();
        when(schedulingService.updateScheduling(dto, user)).thenReturn(new Scheduling());

        controllerSpy.updateScheduling(dto);

        verify(schedulingService).updateScheduling(dto, user);
    }

    @Test
    void listSchedulingsByUser_ShouldReturnList() {
        when(schedulingService.listSchedulingsByUserId(10L)).thenReturn(List.of());

        ResponseEntity<?> response = controllerSpy.listSchedulingsByUser(10L);

        assertEquals(List.of(), response.getBody());
        verify(schedulingService).listSchedulingsByUserId(10L);
    }

    @Test
    void deleteScheduling_ShouldCallService() {
        ResponseEntity<?> response = controllerSpy.deleteScheduling(5L);

        assertEquals(200, response.getStatusCode().value());
        verify(schedulingService).deleteScheduling(5L);
    }
}
