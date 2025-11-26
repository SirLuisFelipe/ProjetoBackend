package com.projeto.demo.repositories;

import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.repositories.projections.DateTurnoCountProjection;
import com.projeto.demo.repositories.projections.PaymentCountProjection;
import com.projeto.demo.repositories.projections.TimelineCountProjection;
import com.projeto.demo.repositories.projections.TrackCountProjection;
import com.projeto.demo.repositories.projections.TurnoCountProjection;
import com.projeto.demo.repositories.projections.UserCountProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SchedulingRepository extends JpaRepository<Scheduling, Long> {

    List<Scheduling> findByUserId(Long id);

    List<Scheduling> findByTrackId(Long trackId);

    // Count de Agendamentos por pista, data e turno
    long countByTrack_IdAndScheduledDateAndTurno(Long trackId,
                                                 LocalDate schedulingDate,
                                                 Scheduling.Turno turno);

    // Consultando agendamentos por data e turno
    List<Scheduling> findByScheduledDateAndTurno(LocalDate Scheduling,
                                                  Scheduling.Turno turno);

    List<Scheduling> findByCheckinStatusAndScheduledDateBefore(Scheduling.CheckinStatus status,
                                                               LocalDate date);

    @Query("SELECT s.turno AS turno, COUNT(s) AS total " +
            "FROM Scheduling s WHERE s.scheduledDate = :date " +
            "AND s.checkinStatus <> com.projeto.demo.entities.Scheduling$CheckinStatus.CANCELADO " +
            "GROUP BY s.turno")
    List<TurnoCountProjection> countByDateGroupedByTurno(@Param("date") LocalDate date);

    @Query("SELECT s.scheduledDate AS date, s.turno AS turno, COUNT(s) AS total " +
            "FROM Scheduling s WHERE s.scheduledDate BETWEEN :startDate AND :endDate " +
            "AND s.checkinStatus <> com.projeto.demo.entities.Scheduling$CheckinStatus.CANCELADO " +
            "GROUP BY s.scheduledDate, s.turno ORDER BY s.scheduledDate")
    List<DateTurnoCountProjection> countByDateRangeGroupedByTurno(@Param("startDate") LocalDate startDate,
                                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT s.track.id AS trackId, s.track.name AS trackName, COUNT(s) AS total " +
            "FROM Scheduling s GROUP BY s.track.id, s.track.name ORDER BY COUNT(s) DESC")
    List<TrackCountProjection> countByTrack();

    @Query("SELECT s.track.id AS trackId, s.track.name AS trackName, COUNT(s) AS total " +
            "FROM Scheduling s WHERE s.scheduledDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.track.id, s.track.name ORDER BY COUNT(s) DESC")
    List<TrackCountProjection> countByTrackBetween(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT s.payment.id AS paymentId, s.payment.name AS paymentName, COUNT(s) AS total " +
            "FROM Scheduling s GROUP BY s.payment.id, s.payment.name ORDER BY COUNT(s) DESC")
    List<PaymentCountProjection> countByPayment();

    @Query("SELECT s.payment.id AS paymentId, s.payment.name AS paymentName, COUNT(s) AS total " +
            "FROM Scheduling s WHERE s.scheduledDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.payment.id, s.payment.name ORDER BY COUNT(s) DESC")
    List<PaymentCountProjection> countByPaymentBetween(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT s.user.id AS userId, s.user.name AS userName, COUNT(s) AS total " +
            "FROM Scheduling s GROUP BY s.user.id, s.user.name ORDER BY COUNT(s) DESC")
    List<UserCountProjection> countByUser(Pageable pageable);

    @Query("SELECT s.user.id AS userId, s.user.name AS userName, COUNT(s) AS total " +
            "FROM Scheduling s WHERE s.scheduledDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.user.id, s.user.name ORDER BY COUNT(s) DESC")
    List<UserCountProjection> countByUserBetween(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 Pageable pageable);

    @Query("SELECT YEAR(s.scheduledDate) AS year, MONTH(s.scheduledDate) AS month, COUNT(s) AS total " +
            "FROM Scheduling s WHERE s.scheduledDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(s.scheduledDate), MONTH(s.scheduledDate) " +
            "ORDER BY YEAR(s.scheduledDate), MONTH(s.scheduledDate)")
    List<TimelineCountProjection> countTimelineBetween(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    long countByScheduledDateBetween(LocalDate startDate, LocalDate endDate);

    long countByScheduledDateBetweenAndCheckinStatus(LocalDate startDate, LocalDate endDate,
                                                     Scheduling.CheckinStatus status);

    @Query("SELECT MIN(s.scheduledDate) FROM Scheduling s")
    LocalDate findMinimumScheduledDate();

    @Query("SELECT MAX(s.scheduledDate) FROM Scheduling s")
    LocalDate findMaximumScheduledDate();
}
