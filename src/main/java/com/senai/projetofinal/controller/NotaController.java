package com.senai.projetofinal.controller;

import com.senai.projetofinal.datasource.entity.NotaEntity;
import com.senai.projetofinal.service.NotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notas")
public class NotaController {

    private final NotaService service;

    public NotaController(NotaService service) {
        this.service = service;
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
}
