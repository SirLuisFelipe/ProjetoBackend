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

        ResponseEntity<Scheduling> response = controllerSpy.createScheduling(dto);

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

        ResponseEntity<java.util.List<Scheduling>> response = controllerSpy.listSchedulingsByUser(10L);

        assertEquals(List.of(), response.getBody());
        verify(schedulingService).listSchedulingsByUserId(10L);
    }

    @Test
    void listSchedulings_ShouldReturnAll() {
        when(schedulingService.listSchedulings()).thenReturn(List.of(new Scheduling()));

        ResponseEntity<java.util.List<Scheduling>> response = controllerSpy.listSchedulings();

        assertEquals(1, response.getBody().size());
        verify(schedulingService).listSchedulings();
    }

    @Test
    void deleteScheduling_ShouldCallService() {
        ResponseEntity<Void> response = controllerSpy.deleteScheduling(5L);

        assertEquals(200, response.getStatusCode().value());
        verify(schedulingService).deleteScheduling(5L);
    }

    @Test
    void createScheduling_ShouldReturnServerError_WhenServiceFails() {
        CreateSchedulingDto dto = new CreateSchedulingDto();
        when(schedulingService.createScheduling(dto)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<Scheduling> response = controllerSpy.createScheduling(dto);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void updateScheduling_ShouldReturnServerError_WhenServiceFails() {
        CreateSchedulingDto dto = new CreateSchedulingDto();
        User user = new User();
        doReturn(user).when(controllerSpy).getLoggedUser();
        when(schedulingService.updateScheduling(dto, user)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<Scheduling> response = controllerSpy.updateScheduling(dto);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void listSchedulings_ShouldReturnServerError_WhenServiceFails() {
        when(schedulingService.listSchedulings()).thenThrow(new RuntimeException("fail"));

        ResponseEntity<List<Scheduling>> response = controllerSpy.listSchedulings();

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void listSchedulingsByUser_ShouldReturnServerError_WhenServiceFails() {
        when(schedulingService.listSchedulingsByUserId(2L)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<List<Scheduling>> response = controllerSpy.listSchedulingsByUser(2L);

        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deleteScheduling_ShouldReturnServerError_WhenServiceFails() {
        doThrow(new RuntimeException("fail")).when(schedulingService).deleteScheduling(10L);

        ResponseEntity<Void> response = controllerSpy.deleteScheduling(10L);

        assertEquals(500, response.getStatusCode().value());
    }
}
