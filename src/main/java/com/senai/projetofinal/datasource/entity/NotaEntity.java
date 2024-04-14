package com.senai.projetofinal.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "nota")
public class NotaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AlunoEntity aluno;

    @ManyToOne
    private DocenteEntity docente;

    @ManyToOne
    private MateriaEntity materia;

    private String valor;

    private LocalDateTime data;
}