package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.materia.AtualizarMateriaRequest;
import com.senai.projetofinal.controller.dto.request.materia.InserirMateriaRequest;
import com.senai.projetofinal.controller.dto.response.materia.MateriaResponse;
import com.senai.projetofinal.datasource.entity.MateriaEntity;
import com.senai.projetofinal.infra.exception.error.NotFoundException;
import com.senai.projetofinal.service.MateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materias")
public class MateriaController {

    private final MateriaService service;

    public MateriaController(MateriaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MateriaEntity>> listarTodasMaterias(
            @RequestHeader("Authorization") String token) {
        List<MateriaEntity> listarMaterias = service.listarTodos(token.substring(7));
        return ResponseEntity.ok().body(listarMaterias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarMateriaPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            MateriaEntity materia = service.buscarPorId(id, token.substring(7));
            return ResponseEntity.ok(materia);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cursos/{curso_id}")
    public ResponseEntity<?> getMateriasByCurso(
            @PathVariable Long curso_id,
            @RequestHeader("Authorization") String token) {
        try {
            List<MateriaEntity> materias = service.buscarMateriasPorCursoId(curso_id, token.substring(7));
            return ResponseEntity.ok(materias);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> criarMateria(
            @RequestBody InserirMateriaRequest inserirMateriaRequest,
            @RequestHeader("Authorization") String token) {
        try {
            MateriaResponse criarMateriaResponse = service.salvar(inserirMateriaRequest, token.substring(7));
            return new ResponseEntity<>(criarMateriaResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMateria(
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
    public ResponseEntity<?> atualizarMateria(
            @PathVariable Long id,
            @RequestBody AtualizarMateriaRequest atualizarMateriaRequest,
            @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(service.atualizar(atualizarMateriaRequest, id, token.substring(7)));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
