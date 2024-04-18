package com.senai.projetofinal.service;

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
}
