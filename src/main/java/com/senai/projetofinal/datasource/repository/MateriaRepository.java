package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.MateriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<MateriaEntity, Long> {

    List<MateriaEntity> findMateriaByCursoId(Long curso_id);

    boolean existsByNome(String nome);
}
