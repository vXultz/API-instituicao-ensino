package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.curso.AtualizarCursoRequest;
import com.senai.projetofinal.controller.dto.request.curso.InserirCursoRequest;
import com.senai.projetofinal.controller.dto.request.docente.AtualizarDocenteRequest;
import com.senai.projetofinal.controller.dto.request.docente.InserirDocenteRequest;
import com.senai.projetofinal.controller.dto.response.curso.CursoResponse;
import com.senai.projetofinal.controller.dto.response.docente.DocenteResponse;
import com.senai.projetofinal.datasource.entity.CursoEntity;
import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import com.senai.projetofinal.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CursoEntity>> listarTodosDocentes(
            @RequestHeader("Authorization") String token) {
        List<CursoEntity> listaCursos = service.listarTodos(token.substring(7));
        return ResponseEntity.ok().body(listaCursos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarCursoPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            CursoEntity curso = service.buscarPorId(id, token.substring(7));
            return ResponseEntity.ok().body(curso);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> criarCurso(
            @RequestBody InserirCursoRequest inserirCursoRequest,
            @RequestHeader("Authorization") String token) {
        try {
            CursoResponse criarCursoResponse = service.salvar(inserirCursoRequest, token.substring(7));
            return new ResponseEntity<>(criarCursoResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCurso(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            service.removerPorId(id, token.substring(7));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCurso(
            @RequestBody AtualizarCursoRequest atualizarCursoRequest,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            CursoEntity atualizarCurso = service.atualizar(atualizarCursoRequest, id, token.substring(7));
            return new ResponseEntity<>(atualizarCurso, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
