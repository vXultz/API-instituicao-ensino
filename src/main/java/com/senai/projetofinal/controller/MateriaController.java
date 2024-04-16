package com.senai.projetofinal.controller;

import com.senai.projetofinal.datasource.entity.MateriaEntity;
import com.senai.projetofinal.service.MateriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
