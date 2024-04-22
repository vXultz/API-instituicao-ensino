package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> {

    boolean existsByNome(String nome);

}
