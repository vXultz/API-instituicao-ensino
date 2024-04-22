package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.aluno.AtualizarAlunoRequest;
import com.senai.projetofinal.controller.dto.request.aluno.InserirAlunoRequest;
import com.senai.projetofinal.controller.dto.response.aluno.AlunoResponse;
import com.senai.projetofinal.datasource.entity.AlunoEntity;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import com.senai.projetofinal.service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AlunoEntity>> listarTodosAlunos(
            @RequestHeader("Authorization") String token) {
        List<AlunoEntity> listarAlunos = service.listarTodos(token.substring(7));
        return ResponseEntity.ok().body(listarAlunos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAlunoPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            AlunoEntity aluno = service.buscarPorId(id, token.substring(7));
            return new ResponseEntity<>(aluno, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> criarAluno(
            @RequestBody InserirAlunoRequest inserirAlunoRequest,
            @RequestHeader("Authorization") String token) {
        try {
            AlunoResponse criarAlunoResponse = service.salvar(inserirAlunoRequest, token.substring(7));
            return new ResponseEntity<>(criarAlunoResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarAluno(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            service.removerPorId(id, token.substring(7));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAluno(
            @PathVariable Long id,
            @RequestBody AtualizarAlunoRequest atualizarAlunoRequest,
            @RequestHeader("Authorization") String token) {
        try {
            AlunoEntity atualizarAlunoResponse = service.atualizar(atualizarAlunoRequest, id, token.substring(7));
            return new ResponseEntity<>(atualizarAlunoResponse, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
