package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.datasource.entity.PapelEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocenteRepository extends JpaRepository<DocenteEntity, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    List<DocenteEntity> findByUsuario_Papel_Nome(PapelEnum papel);

    boolean existsByNome(String nome);
}
