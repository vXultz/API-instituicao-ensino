package com.senai.projetofinal.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "docente")
public class DocenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nome;

    private LocalDate dataEntrada = LocalDate.now();

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;


    @PrePersist
    public void prePersist() {
        if (dataEntrada == null) {
            dataEntrada = LocalDate.now();
        }
    }
}
