package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.TurmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<TurmaEntity, Long> {

    boolean existsByNome(String nome);
}
