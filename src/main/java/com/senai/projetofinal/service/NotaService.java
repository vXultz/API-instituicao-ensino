package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.nota.AtualizarNotaRequest;
import com.senai.projetofinal.controller.dto.request.nota.InserirNotaRequest;
import com.senai.projetofinal.controller.dto.response.nota.NotaResponse;
import com.senai.projetofinal.datasource.entity.AlunoEntity;
import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.datasource.entity.MateriaEntity;
import com.senai.projetofinal.datasource.entity.NotaEntity;
import com.senai.projetofinal.datasource.repository.AlunoRepository;
import com.senai.projetofinal.datasource.repository.DocenteRepository;
import com.senai.projetofinal.datasource.repository.MateriaRepository;
import com.senai.projetofinal.datasource.repository.NotaRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotaService {

    private final NotaRepository repository;

    private final AlunoRepository alunoRepository;

    private final DocenteRepository docenteRepository;

    private final MateriaRepository materiaRepository;

    private final TokenService tokenService;

    public NotaService(NotaRepository repository, AlunoRepository alunoRepository, DocenteRepository docenteRepository, MateriaRepository materiaRepository, TokenService tokenService) {
        this.repository = repository;
        this.alunoRepository = alunoRepository;
        this.docenteRepository = docenteRepository;
        this.materiaRepository = materiaRepository;
        this.tokenService = tokenService;
    }

    public List<NotaEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        List<NotaEntity> notas = repository.findAll();

        if (notas.isEmpty()) {
            throw new NotFoundException("Nenhuma nota encontrada");
        }

        log.info("Todas as notas listada");

        return repository.findAll();
    }

    public NotaEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role) && !"aluno".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        log.info("Nota com id {} encontrada", id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Nota não encontrada"));
    }

    public List<NotaEntity> buscarNotasPorAlunoId(Long aluno_id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role) && !"aluno".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        List<NotaEntity> notasPorAluno = repository.findNotasByAlunoId(aluno_id);

        if (notasPorAluno.isEmpty()) {
            throw new NotFoundException("Nenhuma nota encontrada para o id de aluno passado");
        }

        log.info("Todas as notas do aluno de id {} listadas", aluno_id);
        return notasPorAluno;
    }

    public NotaResponse salvar(InserirNotaRequest inserirNotaRequest, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        if (inserirNotaRequest.valor() == null || inserirNotaRequest.valor().isBlank()) {
            throw new IllegalArgumentException("Valor não pode ser nulo ou vazio");
        }

        AlunoEntity aluno = alunoRepository.findById(inserirNotaRequest.aluno())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado"));

        DocenteEntity docente = docenteRepository.findById(inserirNotaRequest.docente())
                .orElseThrow(() -> new NotFoundException("Docente não encontrado"));

        MateriaEntity materia = materiaRepository.findById(inserirNotaRequest.materia())
                .orElseThrow(() -> new NotFoundException("Matéria não encontrada"));

        NotaEntity nota = new NotaEntity();
        nota.setAluno(aluno);
        nota.setDocente(docente);
        nota.setMateria(materia);
        nota.setValor(inserirNotaRequest.valor());

        NotaEntity notaSalva = repository.save(nota);

        log.info("Salvando nota do aluno {}", notaSalva.getAluno().getNome());

        return new NotaResponse(
                notaSalva.getId(),
                notaSalva.getAluno(),
                notaSalva.getDocente(),
                notaSalva.getMateria(),
                notaSalva.getValor());
    }

    public void removerPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role)) {
            throw new SecurityException("Apenas um usuário admin pode remover uma nota");
        }

        if (!repository.existsById(id)) {
            throw new NotFoundException("Nenhuma nota encontrada com o id passado");
        }

        log.info("Removendo nota com o id {}", id);
        repository.deleteById(id);
    }

    public NotaEntity atualizar(AtualizarNotaRequest atualizarNotaRequest, Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        if (atualizarNotaRequest.valor() == null || atualizarNotaRequest.valor().isBlank()) {
            throw new IllegalArgumentException("Valor não pode ser nulo ou vazio");
        }

        NotaEntity entity = buscarPorId(id, token);

        log.info("Atualizando nota com o id {}", entity.getId());
        entity.setValor(atualizarNotaRequest.valor());
        return repository.save(entity);
    }
}
