package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.docente.AtualizarDocenteRequest;
import com.senai.projetofinal.controller.dto.request.docente.InserirDocenteRequest;
import com.senai.projetofinal.controller.dto.response.docente.DocenteResponse;
import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import com.senai.projetofinal.service.DocenteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/docentes")
public class DocenteController {

    private final DocenteService service;

    public DocenteController(DocenteService docenteService) {
        this.service = docenteService;
    }

    @GetMapping
    public ResponseEntity<List<DocenteEntity>> listarTodosDocentes(
            @RequestHeader("Authorization") String token) {
        List<DocenteEntity> listaDocentes = service.listarTodos(token.substring(7));
        return ResponseEntity.ok().body(listaDocentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarDocentePorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            DocenteEntity docente = service.buscarPorId(id, token.substring(7));
            return ResponseEntity.ok().body(docente);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> criarDocente(
            @RequestBody InserirDocenteRequest inserirDocenteRequest,
            @RequestHeader("Authorization") String token) {
        try {
            DocenteResponse criarDocenteResponse = service.salvar(inserirDocenteRequest, token.substring(7));
            return new ResponseEntity<>(criarDocenteResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarDocente(
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
    public ResponseEntity<?> atualizarDocente(
            @RequestBody AtualizarDocenteRequest atualizarDocenteRequest,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            DocenteEntity atualizarDocente = service.atualizar(atualizarDocenteRequest, id, token.substring(7));
            return new ResponseEntity<>(atualizarDocente, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
