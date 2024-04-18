package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.aluno.AtualizarAlunoRequest;
import com.senai.projetofinal.controller.dto.request.aluno.InserirAlunoRequest;
import com.senai.projetofinal.controller.dto.response.aluno.AlunoResponse;
import com.senai.projetofinal.datasource.entity.AlunoEntity;
import com.senai.projetofinal.datasource.entity.TurmaEntity;
import com.senai.projetofinal.datasource.repository.AlunoRepository;
import com.senai.projetofinal.datasource.repository.TurmaRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AlunoService {

    private final AlunoRepository repository;

    private final TurmaRepository turmaRepository;

    private final TokenService tokenService;

    public AlunoService(AlunoRepository repository, TurmaRepository turmaRepository, TokenService tokenService) {
        this.repository = repository;
        this.turmaRepository = turmaRepository;
        this.tokenService = tokenService;
    }

    public List<AlunoEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        List<AlunoEntity> alunos = repository.findAll();

        if (alunos.isEmpty()) {
            throw new NotFoundException("Nenhum aluno encontrado");
        }

        log.info("Todos os alunos listados");
        return repository.findAll();
    }

    public AlunoEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        log.info("Aluno com id {} encontrado", id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Aluno não encontrado"));
    }

    public AlunoResponse salvar(InserirAlunoRequest inserirAlunoRequest, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        if (inserirAlunoRequest.nome() == null || inserirAlunoRequest.nome().isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        TurmaEntity turma = turmaRepository.findById(inserirAlunoRequest.turma())
                .orElseThrow(() -> new NotFoundException("Turma não encontrada"));

        AlunoEntity aluno = new AlunoEntity();
        aluno.setNome(inserirAlunoRequest.nome());
        aluno.setDataNascimento(inserirAlunoRequest.dataNascimento());
        aluno.setTurma(turma);

        AlunoEntity alunoSalvo = repository.save(aluno);

        log.info("Salvando aluno com o nome {}", inserirAlunoRequest.nome());

        return new AlunoResponse(
                alunoSalvo.getId(),
                alunoSalvo.getNome(),
                alunoSalvo.getDataNascimento(),
                alunoSalvo.getTurma());
    }

    public void removerPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        if (!repository.existsById(id)) {
            throw new NotFoundException("Nenhum aluno encontrado com o id passado");
        }

        log.info("Removendo aluno com id {}", id);
        repository.deleteById(id);
    }

    public AlunoEntity atualizar(AtualizarAlunoRequest atualizarAlunoRequest, Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        if (atualizarAlunoRequest.nome() == null || atualizarAlunoRequest.nome().isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        AlunoEntity entity = buscarPorId(id, token);

        TurmaEntity turma = turmaRepository.findById(atualizarAlunoRequest.turma())
                .orElseThrow(() -> new NotFoundException("Turma não encontrada"));

        log.info("Atualizando aluno com o id {}", id);
        entity.setNome(atualizarAlunoRequest.nome());
        entity.setDataNascimento(atualizarAlunoRequest.dataNascimento());
        entity.setTurma(turma);
        return repository.save(entity);
    }
}