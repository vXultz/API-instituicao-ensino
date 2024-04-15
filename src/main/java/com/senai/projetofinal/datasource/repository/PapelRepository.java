package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.PapelEntity;
import com.senai.projetofinal.datasource.entity.PapelEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PapelRepository extends JpaRepository<PapelEntity, Long> {

    Optional<PapelEntity> findByNome(PapelEnum nome);
}
