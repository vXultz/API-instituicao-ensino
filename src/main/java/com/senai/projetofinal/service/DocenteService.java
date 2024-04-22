package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.docente.AtualizarDocenteRequest;
import com.senai.projetofinal.controller.dto.request.docente.InserirDocenteRequest;
import com.senai.projetofinal.controller.dto.response.docente.DocenteResponse;
import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.datasource.entity.PapelEntity;
import com.senai.projetofinal.datasource.entity.PapelEnum;
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
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        List<DocenteEntity> docentes;

        if ("pedagogico".equals(role) || "recruiter".equals(role)) {
            log.info("Todos os professores listados");
            docentes = repository.findByUsuario_Papel_Nome(PapelEnum.PROFESSOR);
            if (docentes.isEmpty()) {
                log.info("Não há professores cadastrados");
                throw new NotFoundException("Não há professores cadastrados");
            }
        } else {
            log.info("Todos os docentes listados");
            docentes = repository.findAll();
        }

        if (docentes.isEmpty()) {
            log.info("Não há docentes cadastrados");
            throw new NotFoundException("Não há docentes cadastrados");
        }

        return docentes;
    }

    public DocenteEntity buscarPorId(Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"recruiter".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }
        DocenteEntity docente = repository.findById(id).orElseThrow(() -> new NotFoundException("Docente não encontrado"));

        if ("pedagogico".equals(role) || "recruiter".equals(role)) {
            if (docente.getUsuario().getPapel().getNome() == PapelEnum.PROFESSOR) {
                log.info("Professor com id {} encontrado", id);
                return docente;
            } else {
                log.error("Usuários pedagogicos ou recruiters só podem encontrar docente que sejam profressores");
                throw new SecurityException("Usuários pedagogicos ou recruiters só podem encontrar docente que sejam profressores");
            }
        } else {
            log.info("Docente com o id {} encontrado", id);
            return docente;
        }
    }

    public DocenteResponse salvar(InserirDocenteRequest inserirDocenteRequest, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"recruiter".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Usuário não autorizado");
        }

        if (inserirDocenteRequest.nome() == null || inserirDocenteRequest.nome().isBlank()) {
            log.error("Nome não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (repository.existsByNome(inserirDocenteRequest.nome())) {
            log.error("Um docente já existe com o nome: {}", inserirDocenteRequest.nome());
            throw new IllegalArgumentException("Um docente já existe com o nome passado");
        }

        Long usuarioId = Long.valueOf(tokenService.buscaCampo(token, "sub"));

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com o id: {}", usuarioId);
                    return new RuntimeException("Usuário não encontrado");
                });

        UsuarioEntity newDocenteUsuario = usuarioRepository.findById(inserirDocenteRequest.usuario())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado com o id: {}", inserirDocenteRequest.usuario());
                    return new RuntimeException("Usuário não encontrado");
                });

        String newDocentePapel = newDocenteUsuario.getPapel().getNome().toString();

        if (("pedagogico".equals(role) || "recruiter".equals(role)) && !"professor".equals(newDocentePapel)) {
            log.error("Usuário pedagogico ou recruiter só pode salvar um docente com o papel professor");
            throw new SecurityException("Usuário pedagogico ou recruiter só pode salvar um docente com o papel professor");
        }

        if (repository.existsByUsuarioId(inserirDocenteRequest.usuario())) {
            log.debug("Um docente já existe com o Id de usuário: {}", inserirDocenteRequest.usuario());
            throw new RuntimeException("Um docente já existe com o Id de usuário passado");
        }

        DocenteEntity docente = new DocenteEntity();
        UsuarioEntity user = new UsuarioEntity();
        user.setId(inserirDocenteRequest.usuario());
        docente.setUsuario(user);
        docente.setNome(inserirDocenteRequest.nome());

        DocenteEntity docenteSalvo = repository.save(docente);

        log.info("Salvando docente com o nome {}", inserirDocenteRequest.nome());
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

        log.info("Removendo docente com o id {}", id);
        repository.deleteById(id);
    }

    public DocenteEntity atualizar(AtualizarDocenteRequest atualizarDocenteRequest, Long id, String token) {
        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role) && !"pedagogico".equals(role) && !"recruiter".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Tentativa de atualizar não autorizada");
        }

        DocenteEntity entity = buscarPorId(id, token);

        if (atualizarDocenteRequest.nome() == null || atualizarDocenteRequest.nome().isBlank()) {
            log.error("Nome não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }

        if (repository.existsByNome(atualizarDocenteRequest.nome())) {
            log.error("Um docente já existe com o nome: {}", atualizarDocenteRequest.nome());
            throw new IllegalArgumentException("Um docente já existe com o nome passado");
        }

        log.info("Atualizando docente com o id {}", entity.getId());
        entity.setNome(atualizarDocenteRequest.nome());
        return repository.save(entity);
    }
}
