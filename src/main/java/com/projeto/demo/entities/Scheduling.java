package com.projeto.demo.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "scheduling")
public class Scheduling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // Removendo Colunas antigas
    //private LocalDateTime scheduledTimeStart;
    //private LocalDateTime scheduledTimeEnd;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    private Turno turno;


    // Enum para os tipos de turno
    public enum Turno {
        MATUTINO,
        VESPERTINO,
        NOTURNO
    }
}
