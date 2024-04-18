package com.senai.projetofinal.datasource.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "materia")
public class MateriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    @JsonBackReference
    private CursoEntity curso;
}
