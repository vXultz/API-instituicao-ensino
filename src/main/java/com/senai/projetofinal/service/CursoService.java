package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.curso.AtualizarCursoRequest;
import com.senai.projetofinal.controller.dto.request.curso.InserirCursoRequest;
import com.senai.projetofinal.controller.dto.response.curso.CursoResponse;
import com.senai.projetofinal.datasource.entity.CursoEntity;
import com.senai.projetofinal.datasource.repository.CursoRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CursoService {

    private final CursoRepository repository;

    private final TokenService tokenService;

    public CursoService(CursoRepository cursoRepository, TokenService tokenService) {
        this.repository = cursoRepository;
        this.tokenService = tokenService;
    }

    public List<CursoEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        List<CursoEntity> cursos = repository.findAll();

        if(cursos.isEmpty()) {
            log.info("Não há cursos cadastrados");
            throw new NotFoundException("Não há cursos cadastrados");
        }

        log.info("todos os cursos listados");
        return repository.findAll();
    }

    public CursoEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }
        log.info("curso com id {} buscado", id);
        return repository.findById(id).orElseThrow(() -> {
            log.error("Curso não encontrado");
            return new NotFoundException("Curso não encontrado");
        });
    }

    public CursoResponse salvar(InserirCursoRequest inserirCursoRequest, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        if (inserirCursoRequest.nome() == null || inserirCursoRequest.nome().isBlank()) {
            log.error("Nome não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (repository.existsByNome(inserirCursoRequest.nome())) {
            log.error("Já existe um curso com o nome informado");
            throw new IllegalArgumentException("Já existe um curso com o nome informado");
        }

        CursoEntity curso = new CursoEntity();
        curso.setNome(inserirCursoRequest.nome());

        CursoEntity cursoSalvo = repository.save(curso);
        log.info("Salvando curso com o nome {}", inserirCursoRequest.nome());

        return new CursoResponse(
                cursoSalvo.getId(),
                cursoSalvo.getNome()
        );
    }

    public void removerPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");
        if (!"admin".equals(role)) {
            log.error("Apenas um usuário admin pode deletar cursos");
            throw new SecurityException("Apenas um usuário admin pode deletar cursos");
        }

        if (!repository.existsById(id)) {
            log.error("Nenhum curso encontrado com o id passado");
            throw new NotFoundException("Nenhum curso encontrado com o id passado");
        }

        log.info("Removendo curso com o id {}", id);
        repository.deleteById(id);
    }

    public CursoEntity atualizar(AtualizarCursoRequest atualizarCursoRequest, Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Tentativa de atualizar não autorizada");
        }

        CursoEntity entity = buscarPorId(id, token);

        if (atualizarCursoRequest.nome() == null || atualizarCursoRequest.nome().isBlank()) {
            log.error("Nome não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (repository.existsByNome(atualizarCursoRequest.nome())) {
            log.error("Um curso já existe com o nome {}", atualizarCursoRequest.nome());
            throw new IllegalArgumentException("Um curso já existe com o nome passado");
        }

        log.info("Atualizando curso com o id {}", entity.getId());
        entity.setNome(atualizarCursoRequest.nome());
        return repository.save(entity);
    }
}
