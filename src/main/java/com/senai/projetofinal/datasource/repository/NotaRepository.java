package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.NotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<NotaEntity, Long> {

    List<NotaEntity> findNotasByAlunoId(Long aluno_id);

    List<NotaEntity> findNotasByDocenteId(Long docente_id);
}
