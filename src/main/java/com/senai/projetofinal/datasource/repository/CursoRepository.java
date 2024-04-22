package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.CursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<CursoEntity, Long> {

    boolean existsByNome(String nome);
}
