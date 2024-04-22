package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.turma.AtualizarTurmaRequest;
import com.senai.projetofinal.controller.dto.request.turma.InserirTurmaRequest;
import com.senai.projetofinal.controller.dto.response.turma.TurmaResponse;
import com.senai.projetofinal.datasource.entity.CursoEntity;
import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.datasource.entity.TurmaEntity;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;
import com.senai.projetofinal.datasource.repository.CursoRepository;
import com.senai.projetofinal.datasource.repository.DocenteRepository;
import com.senai.projetofinal.datasource.repository.TurmaRepository;
import com.senai.projetofinal.datasource.repository.UsuarioRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TurmaService {

    private final TurmaRepository repository;

    private final UsuarioRepository usuarioRepository;

    private final DocenteRepository docenteRepository;

    private final CursoRepository cursoRepository;

    private final TokenService tokenService;

    public TurmaService(TurmaRepository repository, UsuarioRepository usuarioRepository, DocenteRepository docenteRepository, CursoRepository cursoRepository, TokenService tokenService) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.docenteRepository = docenteRepository;
        this.cursoRepository = cursoRepository;
        this.tokenService = tokenService;
    }

    public List<TurmaEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        List<TurmaEntity> turmas = repository.findAll();

        if (turmas.isEmpty()) {
            log.info("Nenhuma turma encontrada");
            throw new NotFoundException("Nenhuma turma encontrada");
        }

        log.info("Todas as turmas listadas");

        return repository.findAll();
    }

    public TurmaEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        log.info("Turma com o id {} encontrada", id);
        return repository.findById(id).orElseThrow(() -> {
            log.error("Turma não encontrada");
            return new NotFoundException("Turma não encontrada");
        });
    }

    public TurmaResponse salvar(InserirTurmaRequest inserirTurmaRequest, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        if (inserirTurmaRequest.nome() == null || inserirTurmaRequest.nome().isBlank()) {
            log.error("Nome não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (repository.existsByNome(inserirTurmaRequest.nome())) {
            log.error("Uma turma já existe com o nome: {}", inserirTurmaRequest.nome());
            throw new IllegalArgumentException("Uma turma já existe com o nome passado");
        }

        CursoEntity curso = cursoRepository.findById(inserirTurmaRequest.curso())
                .orElseThrow(() -> {
                    log.error("Curso não encontrado");
                    return new NotFoundException("Curso não encontrado");
                });

        DocenteEntity docente = docenteRepository.findById(inserirTurmaRequest.docente())
                .orElseThrow(() -> {
                    log.error("Docente não encontrado");
                    return new NotFoundException("Docente não encontrado");
                });

        String docenteProfessor = docente.getUsuario().getPapel().getNome().toString();

        if (!"professor".equals(docenteProfessor)) {
            log.error("Apenas um docente com papel professor pode ser atribuído a uma turma");
            throw new IllegalArgumentException("Apenas um docente com papel professor pode ser atribuído a uma turma");
        }

        TurmaEntity turma = new TurmaEntity();
        turma.setNome(inserirTurmaRequest.nome());
        turma.setDocente(docente);
        turma.setCurso(curso);

        TurmaEntity turmaSalva = repository.save(turma);

        log.info("Salvando turma com o nome {}", turmaSalva.getNome());

        return new TurmaResponse(
                turmaSalva.getId(),
                turmaSalva.getNome(),
                turmaSalva.getDocente(),
                turmaSalva.getCurso()
        );
    }

    public void removerPorid(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role)) {
            log.error("Apenas um admin pode remover uma turma");
            throw new SecurityException("Apenas um admin pode remover uma turma");
        }

        if (!repository.existsById(id)) {
            log.error("Nenhuma turma encontrada com o id: {}", id);
            throw new NotFoundException("Nenhuma turma encontrada com o id passado");
        }

        log.info("Removendo turma com o id {}", id);
        repository.deleteById(id);
    }

    public TurmaEntity atualizar(AtualizarTurmaRequest atualizarTurmaRequest, Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        if (!repository.existsById(id)) {
            log.error("Nenhuma turma encontrada com o id: {}", id);
            throw new NotFoundException("Nenhuma turma encontrada com o id passado");
        }

        if (atualizarTurmaRequest.nome() == null || atualizarTurmaRequest.nome().isBlank()) {
            log.error("Nome não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (repository.existsByNome(atualizarTurmaRequest.nome())) {
            log.error("Uma turma já existe com o nome: {}", atualizarTurmaRequest.nome());
            throw new IllegalArgumentException("Uma turma já existe com o nome passado");
        }

        TurmaEntity entity = buscarPorId(id, token);

        CursoEntity curso = cursoRepository.findById(atualizarTurmaRequest.curso())
                .orElseThrow(() -> {
                    log.error("Curso não encontrado");
                    return new NotFoundException("Curso não encontrado");
                });

        DocenteEntity docente = docenteRepository.findById(atualizarTurmaRequest.docente())
                .orElseThrow(() -> {
                    log.error("Docente não encontrado");
                    return new NotFoundException("Docente não encontrado");
                });

        String docenteProfessor = docente.getUsuario().getPapel().getNome().toString();

        if (!"professor".equals(docenteProfessor)) {
            log.error("Apenas um docente com papel professor pode ser atribuído a uma turma");
            throw new IllegalArgumentException("Apenas um docente com papel professor pode ser atribuído a uma turma");
        }

        log.info("Atualizando turma com o id {}", entity.getId());

        entity.setNome(atualizarTurmaRequest.nome());
        entity.setDocente(docente);
        entity.setCurso(curso);
        return repository.save(entity);
    }
}
