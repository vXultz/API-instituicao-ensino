package com.senai.projetofinal.service;

import com.senai.projetofinal.datasource.entity.TurmaEntity;
import com.senai.projetofinal.datasource.repository.TurmaRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TurmaService {

    private final TurmaRepository repository;

    private final TokenService tokenService;

    public TurmaService(TurmaRepository repository, TokenService tokenService) {
        this.repository = repository;
        this.tokenService = tokenService;
    }

    public List<TurmaEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        List<TurmaEntity> turmas = repository.findAll();

        if (turmas.isEmpty()) {
            throw new NotFoundException("Nenhuma turma encontrada");
        }

        log.info("Todas as turmas listadas");

        return repository.findAll();
    }

    public TurmaEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        log.info("Turma com o id {} encontrada", id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Turma não encontrada"));
    }
}
