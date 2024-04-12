package com.senai.projetofinal.datasource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "papel")
public class PapelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
}
