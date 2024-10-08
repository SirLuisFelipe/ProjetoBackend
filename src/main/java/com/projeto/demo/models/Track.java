package com.projeto.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "typetrack")
public class Track {
    @Id
    private Integer id;         //Id da pista
    @Column(nullable = false)
    private String name;        // Tipo de pista
}
