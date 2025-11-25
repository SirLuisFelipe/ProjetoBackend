package com.projeto.demo.services;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.dto.SchedulingCancellationStatsDto;
import com.projeto.demo.dto.SchedulingDaySummaryDto;
import com.projeto.demo.dto.SchedulingPaymentSummaryDto;
import com.projeto.demo.dto.SchedulingTimelinePointDto;
import com.projeto.demo.dto.SchedulingTrackSummaryDto;
import com.projeto.demo.dto.SchedulingTurnoSummaryDto;
import com.projeto.demo.dto.SchedulingUserSummaryDto;
import com.projeto.demo.entities.Payment;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.entities.Track;
import com.projeto.demo.entities.User;
import com.projeto.demo.exceptions.NullIdException;
import com.projeto.demo.exceptions.SchedulingNotFoundException;
import com.projeto.demo.exceptions.UnauthorizedActionException;
import com.projeto.demo.repositories.SchedulingRepository;
import com.projeto.demo.repositories.projections.PaymentCountProjection;
import com.projeto.demo.repositories.projections.TimelineCountProjection;
import com.projeto.demo.repositories.projections.TrackCountProjection;
import com.projeto.demo.repositories.projections.TurnoCountProjection;
import com.projeto.demo.repositories.projections.UserCountProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
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

    private static final int CAPACIDADE_POR_TURNO = 25;
    private static final String PAST_SCHEDULING_MESSAGE = "Não é possível manipular agendamentos passados.";

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
        validateSchedulingDate(dto.getScheduledDate());

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
        scheduling.setCheckinStatus(Scheduling.CheckinStatus.PENDENTE);

        return schedulingRepository.save(scheduling);
    }

    /**
     * Atualiza uma reserva existente (mantendo a checagem de disponibilidade).
     */
    @Transactional
    public Scheduling updateScheduling(CreateSchedulingDto dto, User actor) {
        if (dto.getId() == null) {
            throw new NullIdException();
        }

        Scheduling scheduling = findSchedulingById(dto.getId());
        validateSchedulingDate(scheduling.getScheduledDate());
        validateSchedulingDate(dto.getScheduledDate());
        Scheduling.Turno turnoEnum = Scheduling.Turno.valueOf(dto.getTurno().toUpperCase());

        // Se mudar a combinação de pista + data + turno, precisa checar disponibilidade
        Long scheduledTrackId = scheduling.getTrack().getId() == null
                ? null
                : scheduling.getTrack().getId().longValue();
        boolean mudouPista = scheduledTrackId == null
                || !scheduledTrackId.equals(dto.getTrackId());
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

        if (dto.getCheckinStatus() != null && !dto.getCheckinStatus().isBlank()) {
            applyCheckinStatusChange(scheduling, dto.getCheckinStatus(), actor);
        }

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
        validateSchedulingDate(scheduling.getScheduledDate());
        schedulingRepository.delete(scheduling);
    }

    public List<Scheduling> listSchedulingsByUserId(Long userId) {
        return schedulingRepository.findByUserId(userId);
    }

    public List<Scheduling> listSchedulingsByTrackId(Long trackId) {
        return schedulingRepository.findByTrackId(trackId);
    }

    public SchedulingDaySummaryDto getSchedulingSummaryByDay(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date é obrigatório.");
        }

        List<TurnoCountProjection> counts = schedulingRepository.countByDateGroupedByTurno(date);
        EnumMap<Scheduling.Turno, Long> totals = new EnumMap<>(Scheduling.Turno.class);
        counts.forEach(c -> totals.put(c.getTurno(), c.getTotal()));

        List<SchedulingTurnoSummaryDto> summaries = new ArrayList<>();
        for (Scheduling.Turno turno : Scheduling.Turno.values()) {
            summaries.add(new SchedulingTurnoSummaryDto(turno, totals.getOrDefault(turno, 0L)));
        }

        return new SchedulingDaySummaryDto(date, summaries);
    }

    public List<SchedulingTrackSummaryDto> getSchedulingSummaryByTrack() {
        List<TrackCountProjection> counts = schedulingRepository.countByTrack();
        List<SchedulingTrackSummaryDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingTrackSummaryDto(
                c.getTrackId(),
                c.getTrackName(),
                c.getTotal()
        )));
        return result;
    }

    public List<SchedulingPaymentSummaryDto> getSchedulingSummaryByPayment() {
        List<PaymentCountProjection> counts = schedulingRepository.countByPayment();
        List<SchedulingPaymentSummaryDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingPaymentSummaryDto(
                c.getPaymentId(),
                c.getPaymentName(),
                c.getTotal()
        )));
        return result;
    }

    public List<SchedulingUserSummaryDto> getTopUsersByScheduling(int limit) {
        int normalizedLimit = limit <= 0 ? 10 : limit;
        List<UserCountProjection> counts = schedulingRepository.countByUser(PageRequest.of(0, normalizedLimit));
        List<SchedulingUserSummaryDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingUserSummaryDto(
                c.getUserId(),
                c.getUserName(),
                c.getTotal()
        )));
        return result;
    }

    public List<SchedulingTimelinePointDto> getSchedulingTimeline(int months) {
        LocalDate startDate = calculateStartDate(months);
        List<TimelineCountProjection> counts = schedulingRepository.countTimelineFrom(startDate);
        List<SchedulingTimelinePointDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingTimelinePointDto(
                formatYearMonth(c.getYear(), c.getMonth()),
                c.getTotal()
        )));
        return result;
    }

    public SchedulingCancellationStatsDto getCancellationStats(int months) {
        LocalDate startDate = calculateStartDate(months);
        LocalDate endDate = LocalDate.now();

        long total = schedulingRepository.countByScheduledDateBetween(startDate, endDate);
        long cancelled = schedulingRepository.countByScheduledDateBetweenAndCheckinStatus(
                startDate, endDate, Scheduling.CheckinStatus.CANCELADO);
        double percentage = total == 0 ? 0 : (cancelled * 100.0) / total;

        return new SchedulingCancellationStatsDto(
                normalizeMonths(months), total, cancelled, percentage
        );
    }

    public Scheduling updateCheckinStatus(Long schedulingId, String requestedStatus, User actor) {
        Scheduling scheduling = findSchedulingById(schedulingId);
        applyCheckinStatusChange(scheduling, requestedStatus, actor);
        return schedulingRepository.save(scheduling);
    }

    private void applyCheckinStatusChange(Scheduling scheduling, String requestedStatus, User actor) {
        if (requestedStatus == null || requestedStatus.isBlank()) {
            throw new IllegalArgumentException("checkinStatus é obrigatório.");
        }

        if (actor == null) {
            throw new UnauthorizedActionException();
        }

        LocalDate today = LocalDate.now();
        LocalDate scheduledDate = scheduling.getScheduledDate();
        Scheduling.CheckinStatus status = parseCheckinStatus(requestedStatus);
        boolean isAdmin = isAdmin(actor);

        if (status == Scheduling.CheckinStatus.CANCELADO && scheduledDate.isBefore(today)) {
            throw new IllegalStateException("Nao e possivel cancelar agendamentos passados.");
        }

        if (isAdmin) {
            boolean allowed = status == Scheduling.CheckinStatus.CANCELADO
                    || status == Scheduling.CheckinStatus.REALIZADO
                    || status == Scheduling.CheckinStatus.NAO_REALIZADO;
            if (!allowed) {
                throw new IllegalStateException(
                        "Admins so podem definir o status como Cancelado, Realizado ou Nao realizado.");
            }
        } else {
            if (!scheduling.getUser().getId().equals(actor.getId())) {
                throw new UnauthorizedActionException();
            }

            if (status == Scheduling.CheckinStatus.REALIZADO) {
                if (!scheduledDate.isEqual(today)) {
                    throw new IllegalStateException("O check-in so pode ser realizado no dia agendado.");
                }
            } else if (status == Scheduling.CheckinStatus.CANCELADO) {
                // Validacao de data ja feita anteriormente
            } else {
                throw new IllegalStateException("Usuarios so podem atualizar para Realizado ou Cancelado.");
            }
        }

        scheduling.setCheckinStatus(status);
    }

    /**
     * Marca automaticamente como NAO_REALIZADO todos os agendamentos pendentes cujo dia ja passou.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void markPastPendingsAsNaoRealizado() {
        List<Scheduling> overdue = schedulingRepository
                .findByCheckinStatusAndScheduledDateBefore(Scheduling.CheckinStatus.PENDENTE, LocalDate.now());
        if (overdue.isEmpty()) {
            return;
        }

        overdue.forEach(s -> s.setCheckinStatus(Scheduling.CheckinStatus.NAO_REALIZADO));
        schedulingRepository.saveAll(overdue);
    }

    private void validateSchedulingDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("scheduledDate é obrigatório.");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalStateException(PAST_SCHEDULING_MESSAGE);
        }
    }

    private Scheduling.CheckinStatus parseCheckinStatus(String value) {
        try {
            return Scheduling.CheckinStatus.valueOf(normalizeStatusValue(value));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Status de check-in invalido.");
        }
    }

    // Normaliza entrada removendo acentos/espacos para casar com o enum.
    private String normalizeStatusValue(String raw) {
        String trimmed = raw.trim().replace('-', '_').replace(' ', '_');
        String withoutAccents = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutAccents.toUpperCase();
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN");
    }

    private LocalDate calculateStartDate(int months) {
        int normalizedMonths = normalizeMonths(months);
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(normalizedMonths - 1L);
        return startMonth.atDay(1);
    }

    private int normalizeMonths(int months) {
        return months <= 0 ? 6 : months;
    }

    private String formatYearMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            return "";
        }
        return String.format("%04d-%02d", year, month);
    }
}
