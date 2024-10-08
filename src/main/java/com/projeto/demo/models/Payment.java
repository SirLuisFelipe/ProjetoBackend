package com.projeto.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "payment")
public class Payment {

    // Getter para o campo id
    @Id
    private Long id;

    // Getter para o campo type
    private String type;

    // Setter para o campo id
    public void setId(Long id) {
        this.id = id;
    }

    // Setter para o campo type
    public void setType(String type) {
        this.type = type;
    }
}
