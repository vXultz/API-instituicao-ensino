package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.materia.InserirMateriaRequest;
import com.senai.projetofinal.controller.dto.response.materia.MateriaResponse;
import com.senai.projetofinal.datasource.entity.CursoEntity;
import com.senai.projetofinal.datasource.entity.MateriaEntity;
import com.senai.projetofinal.datasource.repository.CursoRepository;
import com.senai.projetofinal.datasource.repository.MateriaRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public MateriaEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        log.info("Matéria com id {} encontrada", id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Matéria não encontrada"));
    }

    public MateriaResponse salvar(InserirMateriaRequest inserirMateriaRequest, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        if (inserirMateriaRequest.nome() == null || inserirMateriaRequest.nome().isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        CursoEntity curso = cursoRepository.findById(inserirMateriaRequest.curso())
                .orElseThrow(() -> new NotFoundException("Curso não encontrado"));

        MateriaEntity materia = new MateriaEntity();
        materia.setNome(inserirMateriaRequest.nome());
        materia.setCurso(curso);

        MateriaEntity materiaSalva = repository.save(materia);

        log.info("Salvando matéria com o nome {}", inserirMateriaRequest.nome());

        return new MateriaResponse(
                materiaSalva.getId(),
                materiaSalva.getNome(),
                materiaSalva.getCurso());
    }

    public void removerPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role)) {
            throw new SecurityException("Usuário não autorizado");
        }

        if (!repository.existsById(id)) {
            throw new NotFoundException("Nenhuma matéria encontrada com o id passado");
        }

        log.info("Removendo matéria com o id {}", id);
        repository.deleteById(id);
    }
}
