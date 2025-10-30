package com.projeto.demo.services;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.entities.Payment;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.Track;
import com.projeto.demo.entities.User;
import com.projeto.demo.exceptions.NullIdException;
import com.projeto.demo.exceptions.SchedulingNotFoundException;
import com.projeto.demo.repositories.SchedulingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulingService {

    @Autowired
    private SchedulingRepository schedulingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TrackService trackService;

    private static final int CAPACIDADE_POR_TURNO = 20;

    /**
     * Verifica se ainda há vagas disponíveis em um determinado dia e turno.
     */
    public boolean checkAvailability(Long trackId, LocalDate date, Scheduling.Turno turno) {
        long qty = schedulingRepository
                .countByTrack_IdAndScheduledDateAndTurno(trackId, date, turno);
        return qty < CAPACIDADE_POR_TURNO;
    }

    /**
     * Cria uma nova reserva, validando a disponibilidade antes de salvar.
     */
    @Transactional
    public Scheduling createScheduling(CreateSchedulingDto dto) {
        Scheduling.Turno turnoEnum = Scheduling.Turno.valueOf(dto.getTurno().toUpperCase());

        if (!checkAvailability(dto.getTrackId(), dto.getScheduledDate(), turnoEnum)) {
            throw new IllegalStateException("Capacidade máxima atingida para este dia/turno.");
        }

        User user = userService.findById(dto.getUserId());
        Track track = trackService.findTrackById(dto.getTrackId());
        Payment payment = (dto.getPaymentId() != null)
                ? paymentService.findPaymentById(dto.getPaymentId())
                : null;

        Scheduling scheduling = new Scheduling();
        scheduling.setUser(user);
        scheduling.setTrack(track);
        scheduling.setPayment(payment);
        scheduling.setScheduledDate(dto.getScheduledDate());
        scheduling.setTurno(turnoEnum);
        scheduling.setPaymentValue(dto.getPaymentValue());

        return schedulingRepository.save(scheduling);
    }

    /**
     * Atualiza uma reserva existente (mantendo a checagem de disponibilidade).
     */
    @Transactional
    public Scheduling updateScheduling(CreateSchedulingDto dto) {
        if (dto.getId() == null) {
            throw new NullIdException();
        }

        Scheduling scheduling = findSchedulingById(dto.getId());
        Scheduling.Turno turnoEnum = Scheduling.Turno.valueOf(dto.getTurno().toUpperCase());

        // Se mudar a combinação de pista + data + turno, precisa checar disponibilidade
        boolean mudouPista = !scheduling.getTrack().getId().equals(dto.getTrackId());
        boolean mudouData = !scheduling.getScheduledDate().equals(dto.getScheduledDate());
        boolean mudouTurno = !scheduling.getTurno().equals(turnoEnum);

        if (mudouPista || mudouData || mudouTurno) {
            if (!checkAvailability(dto.getTrackId(), dto.getScheduledDate(), turnoEnum)) {
                throw new IllegalStateException("Capacidade máxima atingida para este dia/turno.");
            }
        }

        User user = userService.findById(dto.getUserId());
        Track track = trackService.findTrackById(dto.getTrackId());
        Payment payment = (dto.getPaymentId() != null)
                ? paymentService.findPaymentById(dto.getPaymentId())
                : null;

        scheduling.setUser(user);
        scheduling.setTrack(track);
        scheduling.setPayment(payment);
        scheduling.setScheduledDate(dto.getScheduledDate());
        scheduling.setTurno(turnoEnum);
        scheduling.setPaymentValue(dto.getPaymentValue());

        return schedulingRepository.save(scheduling);
    }

    // --- Métodos auxiliares ---

    public Scheduling findSchedulingById(Long id) {
        return schedulingRepository.findById(id)
                .orElseThrow(SchedulingNotFoundException::new);
    }

    public List<Scheduling> listSchedulings() {
        return schedulingRepository.findAll();
    }

    public void deleteScheduling(Long id) {
        Scheduling scheduling = findSchedulingById(id);
        schedulingRepository.delete(scheduling);
    }

    public List<Scheduling> listSchedulingsByUserId(Long userId) {
        return schedulingRepository.findByUserId(userId);
    }

    public List<Scheduling> listSchedulingsByTrackId(Long trackId) {
        return schedulingRepository.findByTrackId(trackId);
    }
}
