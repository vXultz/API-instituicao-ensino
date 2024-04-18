package com.senai.projetofinal.controller;

import com.senai.projetofinal.datasource.entity.TurmaEntity;
import com.senai.projetofinal.service.TurmaService;
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
    public ResponseEntity<TurmaEntity> buscarTurmaPorId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        TurmaEntity turma = service.buscarPorId(id, token.substring(7));
        return ResponseEntity.ok(turma);
    }
}
