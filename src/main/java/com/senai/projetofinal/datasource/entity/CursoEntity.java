package com.senai.projetofinal.datasource.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    private List<TurmaEntity> turmas;

    @OneToMany(mappedBy = "curso", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<MateriaEntity> materias;
}
