package com.senai.projetofinal.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
@Table(name = "curso")
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nome;

    @OneToMany(mappedBy = "curso", fetch = FetchType.EAGER)
    private List<TurmaEntity> turmas;

    @OneToMany(mappedBy = "curso", fetch = FetchType.EAGER)
    private List<MateriaEntity> materias;
}
