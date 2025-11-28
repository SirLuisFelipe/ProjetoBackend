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
import com.projeto.demo.exceptions.PaymentNotFoundException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.TreeMap;

@Service
public class SchedulingService {

    private final SchedulingRepository schedulingRepository;
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final TrackService trackService;

    public SchedulingService(SchedulingRepository schedulingRepository,
                             UserService userService,
                             PaymentRepository paymentRepository,
                             TrackService trackService) {
        this.schedulingRepository = schedulingRepository;
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.trackService = trackService;
    }

    private static final int CAPACIDADE_POR_TURNO = 25;
    private static final String PAST_SCHEDULING_MESSAGE = "Não é possível manipular agendamentos passados.";

    /**
     * Verifica se ainda há vagas disponíveis em um determinado dia e turno.
     */
    public boolean checkAvailability(LocalDate date, Scheduling.Turno turno) {
        long qty = schedulingRepository
                .countByScheduledDateAndTurno(date, turno);
        return qty < CAPACIDADE_POR_TURNO;
    }

    /**
     * Cria uma nova reserva, validando a disponibilidade antes de salvar.
     */
    @Transactional
    public Scheduling createScheduling(CreateSchedulingDto dto) {
        validateSchedulingDate(dto.getScheduledDate());

        Scheduling.Turno turnoEnum = Scheduling.Turno.valueOf(dto.getTurno().toUpperCase());

        if (!checkAvailability(dto.getScheduledDate(), turnoEnum)) {
            throw new IllegalStateException("Capacidade máxima atingida para este dia/turno.");
        }

        User user = userService.findById(dto.getUserId());
        Track track = trackService.findTrackById(dto.getTrackId());
        Payment payment = resolvePayment(dto.getPaymentId());

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
        boolean mudouData = !scheduling.getScheduledDate().equals(dto.getScheduledDate());
        boolean mudouTurno = !scheduling.getTurno().equals(turnoEnum);

        if ((mudouData || mudouTurno) && !checkAvailability(dto.getScheduledDate(), turnoEnum)) {
            throw new IllegalStateException("Capacidade máxima atingida para este dia/turno.");
        }

        User user = userService.findById(dto.getUserId());
        Track track = trackService.findTrackById(dto.getTrackId());
        Payment payment = resolvePayment(dto.getPaymentId());

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

    public List<SchedulingDaySummaryDto> getSchedulingSummaryByDayRange(LocalDate startDate, LocalDate endDate) {
        DateRange range = resolveRangeWithDataset(startDate, endDate);
        if (range == null) {
            return List.of();
        }

        List<DateTurnoCountProjection> counts = schedulingRepository
                .countByDateRangeGroupedByTurno(range.start(), range.end());

        TreeMap<LocalDate, EnumMap<Scheduling.Turno, Long>> byDate = new TreeMap<>();
        counts.forEach(c -> {
            EnumMap<Scheduling.Turno, Long> totals = byDate
                    .computeIfAbsent(c.getDate(), d -> new EnumMap<>(Scheduling.Turno.class));
            totals.put(c.getTurno(), c.getTotal());
        });

        List<SchedulingDaySummaryDto> summaries = new ArrayList<>();
        LocalDate current = range.start();
        while (!current.isAfter(range.end())) {
            EnumMap<Scheduling.Turno, Long> totals = byDate.getOrDefault(current, new EnumMap<>(Scheduling.Turno.class));
            List<SchedulingTurnoSummaryDto> turnos = new ArrayList<>();
            for (Scheduling.Turno turno : Scheduling.Turno.values()) {
                turnos.add(new SchedulingTurnoSummaryDto(turno, totals.getOrDefault(turno, 0L)));
            }
            summaries.add(new SchedulingDaySummaryDto(current, turnos));
            current = current.plusDays(1);
        }

        return summaries;
    }

    public List<SchedulingTrackSummaryDto> getSchedulingSummaryByTrack(LocalDate startDate, LocalDate endDate) {
        DateRange range = normalizeOptionalRange(startDate, endDate);
        List<TrackCountProjection> counts;
        if (range.start() == null) {
            counts = schedulingRepository.countByTrack();
        } else {
            counts = schedulingRepository.countByTrackBetween(range.start(), range.end());
        }
        List<SchedulingTrackSummaryDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingTrackSummaryDto(
                c.getTrackId(),
                c.getTrackName(),
                c.getTotal()
        )));
        return result;
    }

    public List<SchedulingPaymentSummaryDto> getSchedulingSummaryByPayment(LocalDate startDate, LocalDate endDate) {
        DateRange range = normalizeOptionalRange(startDate, endDate);
        List<PaymentCountProjection> counts;
        if (range.start() == null) {
            counts = schedulingRepository.countByPayment();
        } else {
            counts = schedulingRepository.countByPaymentBetween(range.start(), range.end());
        }
        List<SchedulingPaymentSummaryDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingPaymentSummaryDto(
                c.getPaymentId(),
                c.getPaymentName(),
                c.getTotal()
        )));
        return result;
    }

    public List<SchedulingUserSummaryDto> getTopUsersByScheduling(int limit,
                                                                  LocalDate startDate,
                                                                  LocalDate endDate) {
        int normalizedLimit = limit <= 0 ? 10 : limit;
        DateRange range = normalizeOptionalRange(startDate, endDate);
        List<UserCountProjection> counts;
        if (range.start() == null) {
            counts = schedulingRepository.countByUser(PageRequest.of(0, normalizedLimit));
        } else {
            counts = schedulingRepository.countByUserBetween(range.start(), range.end(),
                    PageRequest.of(0, normalizedLimit));
        }
        List<SchedulingUserSummaryDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingUserSummaryDto(
                c.getUserId(),
                c.getUserName(),
                c.getTotal()
        )));
        return result;
    }

    public List<SchedulingTimelinePointDto> getSchedulingTimeline(LocalDate startDate,
                                                                   LocalDate endDate) {
        DateRange range = resolveRangeWithDataset(startDate, endDate);
        if (range == null) {
            return List.of();
        }
        List<TimelineCountProjection> counts = schedulingRepository
                .countTimelineBetween(range.start(), range.end());
        List<SchedulingTimelinePointDto> result = new ArrayList<>();
        counts.forEach(c -> result.add(new SchedulingTimelinePointDto(
                formatYearMonth(c.getYear(), c.getMonth()),
                c.getTotal()
        )));
        return result;
    }

    public SchedulingCancellationStatsDto getCancellationStats(LocalDate startDate,
                                                               LocalDate endDate) {
        DateRange range = resolveRangeWithDataset(startDate, endDate);
        if (range == null) {
            return new SchedulingCancellationStatsDto(0, 0L, 0L, 0);
        }

        long total = schedulingRepository.countByScheduledDateBetween(range.start(), range.end());
        long cancelled = schedulingRepository.countByScheduledDateBetweenAndCheckinStatus(
                range.start(), range.end(), Scheduling.CheckinStatus.CANCELADO);
        double percentage = total == 0 ? 0 : (cancelled * 100.0) / total;

        int monthsValue = calculateMonthsBetween(range.start(), range.end());

        return new SchedulingCancellationStatsDto(
                monthsValue, total, cancelled, percentage
        );
    }

    public Scheduling updateCheckinStatus(Long schedulingId, String requestedStatus, User actor) {
        Scheduling scheduling = findSchedulingById(schedulingId);
        applyCheckinStatusChange(scheduling, requestedStatus, actor);
        return schedulingRepository.save(scheduling);
    }

    private void applyCheckinStatusChange(Scheduling scheduling, String requestedStatus, User actor) {
        ensureRequestedStatusProvided(requestedStatus);
        ensureActorPresent(actor);

        Scheduling.CheckinStatus status = parseCheckinStatus(requestedStatus);
        LocalDate today = LocalDate.now();

        blockPastCancellations(scheduling, status, today);
        enforceRoleSpecificRules(scheduling, status, actor, today);

        scheduling.setCheckinStatus(status);
    }

    private void ensureRequestedStatusProvided(String requestedStatus) {
        if (requestedStatus == null || requestedStatus.isBlank()) {
            throw new IllegalArgumentException("checkinStatus é obrigatório.");
        }
    }

    private void ensureActorPresent(User actor) {
        if (actor == null) {
            throw new UnauthorizedActionException();
        }
    }

    private void blockPastCancellations(Scheduling scheduling, Scheduling.CheckinStatus status, LocalDate today) {
        if (status == Scheduling.CheckinStatus.CANCELADO && scheduling.getScheduledDate().isBefore(today)) {
            throw new IllegalStateException("Nao e possivel cancelar agendamentos passados.");
        }
    }

    private void enforceRoleSpecificRules(Scheduling scheduling,
                                          Scheduling.CheckinStatus status,
                                          User actor,
                                          LocalDate today) {
        if (isAdmin(actor)) {
            validateAdminAllowedStatus(status);
            return;
        }
        ensureOwnership(scheduling, actor);
        validateUserAllowedStatus(status, scheduling.getScheduledDate(), today);
    }

    private void validateAdminAllowedStatus(Scheduling.CheckinStatus status) {
        boolean allowed = status == Scheduling.CheckinStatus.CANCELADO
                || status == Scheduling.CheckinStatus.REALIZADO
                || status == Scheduling.CheckinStatus.NAO_REALIZADO;
        if (!allowed) {
            throw new IllegalStateException(
                    "Admins so podem definir o status como Cancelado, Realizado ou Nao realizado.");
        }
    }

    private void ensureOwnership(Scheduling scheduling, User actor) {
        if (!scheduling.getUser().getId().equals(actor.getId())) {
            throw new UnauthorizedActionException();
        }
    }

    private void validateUserAllowedStatus(Scheduling.CheckinStatus status,
                                           LocalDate scheduledDate,
                                           LocalDate today) {
        if (status == Scheduling.CheckinStatus.REALIZADO) {
            if (!scheduledDate.isEqual(today)) {
                throw new IllegalStateException("O check-in so pode ser realizado no dia agendado.");
            }
            return;
        }

        if (status != Scheduling.CheckinStatus.CANCELADO) {
            throw new IllegalStateException("Usuarios so podem atualizar para Realizado ou Cancelado.");
        }
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

    private String formatYearMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            return "";
        }
        return String.format("%04d-%02d", year, month);
    }

    private DateRange normalizeOptionalRange(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return new DateRange(null, null);
        }
        return requireRange(start, end);
    }

    private DateRange requireRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("startDate e endDate devem ser informados.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endDate nao pode ser anterior ao startDate.");
        }
        return new DateRange(start, end);
    }

    private DateRange resolveRangeWithDataset(LocalDate start, LocalDate end) {
        if (start != null || end != null) {
            return requireRange(start, end);
        }
        LocalDate minDate = schedulingRepository.findMinimumScheduledDate();
        LocalDate maxDate = schedulingRepository.findMaximumScheduledDate();
        if (minDate == null || maxDate == null) {
            return null;
        }
        return new DateRange(minDate, maxDate);
    }

    private int calculateMonthsBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return (int) ChronoUnit.MONTHS.between(YearMonth.from(start), YearMonth.from(end)) + 1;
    }

    private Payment resolvePayment(Long paymentId) {
        if (paymentId == null) {
            return null;
        }
        return paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);
    }

    private record DateRange(LocalDate start, LocalDate end) {}
}
