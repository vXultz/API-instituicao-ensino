package com.senai.projetofinal.datasource.repository;

import com.senai.projetofinal.datasource.entity.DocenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<DocenteEntity, Long> {

    Optional<DocenteEntity> findByNome(String nome);
}
