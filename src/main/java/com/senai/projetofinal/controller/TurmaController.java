package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.turma.AtualizarTurmaRequest;
import com.senai.projetofinal.controller.dto.request.turma.InserirTurmaRequest;
import com.senai.projetofinal.controller.dto.response.turma.TurmaResponse;
import com.senai.projetofinal.datasource.entity.TurmaEntity;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import com.senai.projetofinal.service.TurmaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaService service;

    public TurmaController(TurmaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TurmaEntity>> listarTodasTurmas(
            @RequestHeader("Authorization") String token) {
        List<TurmaEntity> listarTurmas = service.listarTodos(token.substring(7));
        return ResponseEntity.ok().body(listarTurmas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarTurmaPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            TurmaEntity turma = service.buscarPorId(id, token.substring(7));
            return ResponseEntity.ok(turma);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> criarTurma(
            @RequestBody InserirTurmaRequest inserirTurmaRequest,
            @RequestHeader("Authorization") String token) {
        try {
            TurmaResponse criarTurmaResponse = service.salvar(inserirTurmaRequest, token.substring(7));
            return new ResponseEntity<>(criarTurmaResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarTurma(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            service.removerPorid(id, token.substring(7));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarTurma(
            @PathVariable Long id,
            @RequestBody AtualizarTurmaRequest atualizarTurmaRequest,
            @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(service.atualizar(atualizarTurmaRequest, id, token.substring(7)));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
