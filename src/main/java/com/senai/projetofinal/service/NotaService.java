package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.nota.AtualizarNotaRequest;
import com.senai.projetofinal.controller.dto.request.nota.InserirNotaRequest;
import com.senai.projetofinal.controller.dto.response.nota.NotaResponse;
import com.senai.projetofinal.datasource.entity.*;
import com.senai.projetofinal.datasource.repository.*;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class NotaService {

    private final NotaRepository repository;

    private final AlunoRepository alunoRepository;
    private final AlunoService alunoService;

    private final DocenteRepository docenteRepository;

    private final MateriaRepository materiaRepository;

    private final TurmaRepository turmaRepository;

    private final TokenService tokenService;

    public NotaService(NotaRepository repository, AlunoRepository alunoRepository, AlunoService alunoService, DocenteRepository docenteRepository, MateriaRepository materiaRepository, TurmaRepository turmaRepository, TokenService tokenService) {
        this.repository = repository;
        this.alunoRepository = alunoRepository;
        this.alunoService = alunoService;
        this.docenteRepository = docenteRepository;
        this.materiaRepository = materiaRepository;
        this.turmaRepository = turmaRepository;
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

        boolean materiaPertenceAoCurso = aluno.getTurma().getCurso().getMaterias()
                .stream().anyMatch(m -> m.getId().equals(materia.getId()));

        if(!materiaPertenceAoCurso){
            throw new IllegalArgumentException("A matéria não pertence ao curso da turma que o aluno está matriculado");
        }

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



    public BigDecimal calcularPontuacao(Long aluno_id, String token) {
        AlunoEntity aluno = alunoService.buscarPorId(aluno_id, token);
        List<NotaEntity> notasPorAluno = buscarNotasPorAlunoId(aluno_id, token);
        CursoEntity cursoTurma = buscarCursoPorTurmaId(aluno.getTurma().getId());

        int totalMaterias = cursoTurma.getMaterias().size();

        BigDecimal soma = BigDecimal.ZERO;
        for (NotaEntity nota : notasPorAluno) {
            BigDecimal valorNota = new BigDecimal(nota.getValor());
            soma = soma.add(valorNota);
        }

        if (notasPorAluno.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal media = soma.divide(BigDecimal.valueOf(totalMaterias), RoundingMode.HALF_UP);
        return media.multiply(BigDecimal.TEN);
    }

    public CursoEntity buscarCursoPorTurmaId(Long turmaId) {
        Optional<TurmaEntity> turma = turmaRepository.findById(turmaId);

        if (turma.isEmpty()) {
            throw new NotFoundException("Turma não encontrada");
        }

        return turma.get().getCurso();
    }
}
