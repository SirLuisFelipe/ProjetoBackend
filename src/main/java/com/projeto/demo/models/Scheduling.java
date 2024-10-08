package com.projeto.demo.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "scheduling")
public class Scheduling {

    // Chave primaria, Gerada automaticamente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento ManyToOne onde muitos agendamentos podem pertencer a um usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Armazena o id da pista/Track diretamente (sem criar relacionamento com entidade Track)
    @Column(name = "trackid", nullable = false)
    private Integer trackid;

    // Armazena data de agendamento
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    // Armazena hora de início
    @Column(nullable = false)
    private LocalDateTime start;

    // Armazena hora de fim
    @Column(nullable = false)
    private LocalDateTime end;

    // Relacionamento ManyToOne com Payment
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paymentid", nullable = false)
    private Payment payment;

    // Armazena o valor do pagamento
    @Column(name = "payment_value", nullable = true)
    private BigDecimal paymentValue;

    // Getters e Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTrackid() {
        return trackid;
    }

    public void setTrackid(Integer trackid) {
        this.trackid = trackid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public BigDecimal getPaymentValue() {
        return paymentValue;
    }

    public void setPaymentValue(BigDecimal paymentValue) {
        this.paymentValue = paymentValue;
    }
}
