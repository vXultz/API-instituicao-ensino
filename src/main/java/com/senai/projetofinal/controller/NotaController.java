package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.nota.AtualizarNotaRequest;
import com.senai.projetofinal.controller.dto.request.nota.InserirNotaRequest;
import com.senai.projetofinal.controller.dto.response.nota.NotaResponse;
import com.senai.projetofinal.datasource.entity.NotaEntity;
import com.senai.projetofinal.service.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/notas")
public class NotaController {

    private final NotaService service;

    public NotaController(NotaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<NotaEntity>> listarTodasNotas(
            @RequestHeader("Authorization") String token) {
        List<NotaEntity> listarNotas = service.listarTodos(token.substring(7));
        return ResponseEntity.ok().body(listarNotas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaEntity> buscarNotaPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        NotaEntity nota = service.buscarPorId(id, token.substring(7));
        return ResponseEntity.ok(nota);
    }

    @GetMapping("/alunos/{aluno_id}")
    public ResponseEntity<List<NotaEntity>> getNotasByAluno(
            @PathVariable Long aluno_id,
            @RequestHeader("Authorization") String token) {
        List<NotaEntity> notas = service.buscarNotasPorAlunoId(aluno_id, token.substring(7));
        return ResponseEntity.ok(notas);
    }

    @GetMapping("/alunos/{aluno_id}/pontuacao")
    public ResponseEntity<BigDecimal> getPontuacaoByAluno(
            @PathVariable Long aluno_id,
            @RequestHeader("Authorization") String token) {
        BigDecimal pontuacao = service.calcularPontuacao(aluno_id, token.substring(7));
        return ResponseEntity.ok(pontuacao);
    }

    @PostMapping
    public ResponseEntity<?> criarNota(
            @RequestBody InserirNotaRequest inserirNotaRequest,
            @RequestHeader("Authorization") String token) {
        try {
            NotaResponse criarNotaResponse = service.salvar(inserirNotaRequest, token.substring(7));
            return new ResponseEntity<>(criarNotaResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarNota(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        service.removerPorId(id, token.substring(7));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotaEntity> atualizarNota(
            @PathVariable Long id,
            @RequestBody AtualizarNotaRequest atualizarNotaRequest,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.atualizar(atualizarNotaRequest, id, token.substring(7)));
    }

}
