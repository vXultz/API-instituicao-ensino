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
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class NotaService {

    private final NotaRepository repository;

    private final AlunoRepository alunoRepository;

    private final DocenteRepository docenteRepository;

    private final MateriaRepository materiaRepository;

    private final TurmaRepository turmaRepository;

    private final TokenService tokenService;

    public NotaService(NotaRepository repository, AlunoRepository alunoRepository, AlunoService alunoService, DocenteRepository docenteRepository, MateriaRepository materiaRepository, TurmaRepository turmaRepository, TokenService tokenService) {
        this.repository = repository;
        this.alunoRepository = alunoRepository;
        this.docenteRepository = docenteRepository;
        this.materiaRepository = materiaRepository;
        this.turmaRepository = turmaRepository;
        this.tokenService = tokenService;
    }

    public List<NotaEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        List<NotaEntity> notas = repository.findAll();

        if (notas.isEmpty()) {
            log.info("Nenhuma nota encontrada");
            throw new NotFoundException("Nenhuma nota encontrada");
        }

        log.info("Todas as notas listada");

        return repository.findAll();
    }

    public NotaEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        log.info("Nota com id {} encontrada", id);
        return repository.findById(id).orElseThrow(() -> {
            log.error("Nota não encontrada com o id: {}", id);
            return new NotFoundException("Nota não encontrada");
        });
    }

    public List<NotaEntity> buscarNotasPorAlunoId(Long aluno_id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role) && !"aluno".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        Long usuario = Long.valueOf(tokenService.buscaCampo(token, "sub"));

        AlunoEntity aluno = alunoRepository.findById(aluno_id)
                .orElseThrow(() -> {
                    log.error("Aluno não encontrado com o id: {}", aluno_id);
                    return new NotFoundException("Aluno não encontrado");
                });

        Long usuarioAluno = aluno.getUsuario().getId();

        if ("admin".equals(role) || "pedagogico".equals(role) || "professor".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            return repository.findNotasByAlunoId(aluno_id);
        } else if (!Objects.equals(usuario, usuarioAluno)) {
            log.error("Apenas notas com o seu Id podem ser acessadas");
            throw new SecurityException("Apenas notas com o seu Id podem ser acessadas");
        }

        List<NotaEntity> notasPorAluno = repository.findNotasByAlunoId(aluno_id);

        if (notasPorAluno.isEmpty()) {
            log.error("Nenhuma nota encontrada para o id de aluno passado");
            throw new NotFoundException("Nenhuma nota encontrada para o id de aluno passado");
        }

        log.info("Todas as notas do aluno de id {} listadas", aluno_id);
        return notasPorAluno;
    }

    public List<NotaEntity> buscarNotasPorDocenteId(Long docente_id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        Long usuario = Long.valueOf(tokenService.buscaCampo(token, "sub"));

        DocenteEntity docente = docenteRepository.findById(docente_id)
                .orElseThrow(() -> {
                    log.error("Docente não encontrado");
                    return new NotFoundException("Docente não encontrado");
                });

        Long usuarioDocente = docente.getUsuario().getId();

        if ("admin".equals(role) || "pedagogico".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            return repository.findNotasByDocenteId(docente_id);
        } else if (!Objects.equals(usuario, usuarioDocente)) {
            log.error("Apenas notas com o seu Id podem ser acessadas");
            throw new SecurityException("Apenas notas com o seu Id podem ser acessadas");
        }

        List<NotaEntity> notasPorDocente = repository.findNotasByDocenteId(docente_id);

        if (notasPorDocente.isEmpty()) {
            log.error("Nenhuma nota encontrada para o id de docente passado");
            throw new NotFoundException("Nenhuma nota encontrada para o id de docente passado");
        }

        log.info("Todas as notas do docente de id {} listadas", docente_id);
        return notasPorDocente;
    }


    public NotaResponse salvar(InserirNotaRequest inserirNotaRequest, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        if (inserirNotaRequest.valor() == null || inserirNotaRequest.valor().isBlank()) {
            log.error("Valor não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Valor não pode ser nulo ou vazio");
        }

        AlunoEntity aluno = alunoRepository.findById(inserirNotaRequest.aluno())
                .orElseThrow(() -> {
                    log.error("Aluno não encontrado");
                    return new NotFoundException("Aluno não encontrado");
                });

        DocenteEntity docente = docenteRepository.findById(inserirNotaRequest.docente())
                .orElseThrow(() -> {
                    log.error("Docente não encontrado");
                    return new NotFoundException("Docente não encontrado");
                });

        if (!"professor".equals(docente.getUsuario().getPapel().getNome().toString())
                && !"admin".equals(docente.getUsuario().getPapel().getNome().toString())) {
            log.error("O docente a ser salvo na nota não é um usuário admin ou professor");
            throw new SecurityException("O docente a ser salvo na nota não é um usuário admin ou professor");
        }

        MateriaEntity materia = materiaRepository.findById(inserirNotaRequest.materia())
                .orElseThrow(() -> {
                    log.error("Matéria não encontrada");
                    return new NotFoundException("Matéria não encontrada");
                });

        boolean materiaPertenceAoCurso = aluno.getTurma().getCurso().getMaterias()
                .stream().anyMatch(m -> m.getId().equals(materia.getId()));

        if(!materiaPertenceAoCurso){
            log.error("A matéria não pertence ao curso da turma que o aluno está matriculado");
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
            log.error("Apenas um usuário admin pode remover uma nota");
            throw new SecurityException("Apenas um usuário admin pode remover uma nota");
        }

        if (!repository.existsById(id)) {
            log.error("Nenhuma nota encontrada com o id: {}", id);
            throw new NotFoundException("Nenhuma nota encontrada com o id passado");
        }

        log.info("Removendo nota com o id {}", id);
        repository.deleteById(id);
    }

    public NotaEntity atualizar(AtualizarNotaRequest atualizarNotaRequest, Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        NotaEntity entity = buscarPorId(id, token);

        if (atualizarNotaRequest.valor() == null || atualizarNotaRequest.valor().isBlank()) {
            log.error("Valor não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Valor não pode ser nulo ou vazio");
        }

        log.info("Atualizando nota com o id {}", entity.getId());
        entity.setValor(atualizarNotaRequest.valor());
        return repository.save(entity);
    }


    public BigDecimal calcularPontuacao(Long aluno_id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"professor".equals(role) && !"aluno".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        Long usuario = Long.valueOf(tokenService.buscaCampo(token, "sub"));

        AlunoEntity aluno = alunoRepository.findById(aluno_id)
                .orElseThrow(() -> {
                    log.error("Aluno não encontrado");
                    return new NotFoundException("Aluno não encontrado");
                });

        Long usuarioAluno = aluno.getUsuario().getId();


        if ("aluno".equals(role) && !Objects.equals(usuario, usuarioAluno)) {
            log.error("Apenas pontuação com o seu Id podem ser acessadas");
            throw new SecurityException("Apenas pontuação com o seu Id podem ser acessadas");
        }

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
            log.error("Turma não encontrada com o id: {}", turmaId);
            throw new NotFoundException("Turma não encontrada");
        }

        return turma.get().getCurso();
    }
}
