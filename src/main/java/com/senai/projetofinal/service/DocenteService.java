package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.docente.AtualizarDocenteRequest;
import com.senai.projetofinal.controller.dto.request.docente.InserirDocenteRequest;
import com.senai.projetofinal.controller.dto.response.docente.DocenteResponse;
import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;
import com.senai.projetofinal.datasource.repository.DocenteRepository;
import com.senai.projetofinal.datasource.repository.UsuarioRepository;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocenteService {

    private final DocenteRepository repository;

    private final UsuarioRepository usuarioRepository;

    private final TokenService tokenService;

    public DocenteService(DocenteRepository docenteRepository, UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.repository = docenteRepository;
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    public List<DocenteEntity> listarTodos(String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"recruiter".equals(role)) {
            throw new SecurityException("Tentativa de atualizar não autorizada");
        }
        log.info("todos os docentees listados");
        return repository.findAll();
    }

    public DocenteEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"recruiter".equals(role)) {
            throw new SecurityException("Tentativa de atualizar não autorizada");
        }
        log.info("docente com id {} buscado", id);
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Docente não encontrado"));
    }

    public DocenteResponse salvar(InserirDocenteRequest inserirDocenteRequest, String token) {

        if (inserirDocenteRequest.nome() == null || inserirDocenteRequest.nome().isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        Long usuarioId = Long.valueOf(tokenService.buscaCampo(token, "sub"));

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (repository.existsByUsuarioId(inserirDocenteRequest.usuario())) {
            throw new RuntimeException("Um docente já existe com o Id de usuário passado");
        }

        DocenteEntity docente = new DocenteEntity();
        UsuarioEntity user = new UsuarioEntity();
        user.setId(inserirDocenteRequest.usuario());
        docente.setUsuario(user);
        docente.setNome(inserirDocenteRequest.nome());

        DocenteEntity docenteSalvo = repository.save(docente);

        log.info("salvando docente com o nome {}", inserirDocenteRequest.nome());
        return new DocenteResponse(
                docenteSalvo.getId(),
                docenteSalvo.getNome(),
                docenteSalvo.getUsuario()
                );
    }

    public void removerPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");
        if (!"admin".equals(role)) {
            throw new SecurityException("Apenas um usuário admin pode deletar docentes");
        }

        if (!repository.existsById(id)) {
            throw new NotFoundException("Nenhum docente encontrado com o id passado");
        }

        log.info("removendo docente com o id {}", id);
        repository.deleteById(id);
    }

    public DocenteEntity atualizar(AtualizarDocenteRequest atualizarDocenteRequest, Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"recruiter".equals(role)) {
            throw new SecurityException("Tentativa de atualizar não autorizada");
        }

        if (atualizarDocenteRequest.nome().isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        DocenteEntity entity = buscarPorId(id, token);

        log.info("atualizando docente com o id {}", entity.getId());
        entity.setNome(atualizarDocenteRequest.nome());
        return repository.save(entity);
    }
}
