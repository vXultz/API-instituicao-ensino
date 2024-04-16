package com.senai.projetofinal.service;

import com.senai.projetofinal.datasource.entity.MateriaEntity;
import com.senai.projetofinal.datasource.repository.CursoRepository;
import com.senai.projetofinal.datasource.repository.MateriaRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MateriaService {

    private final MateriaRepository repository;

    private final CursoRepository cursoRepository;

    private final TokenService tokenService;

    public MateriaService(MateriaRepository repository, CursoRepository cursoRepository, TokenService tokenService) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
        this.tokenService = tokenService;
    }

    public List<MateriaEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        List<MateriaEntity> materias = repository.findAll();

        if (materias.isEmpty()) {
            throw new NotFoundException("Nenhuma matéria encontrada");
        }

        log.info("Todas as matérias listadas");

        return repository.findAll();
    }
}
