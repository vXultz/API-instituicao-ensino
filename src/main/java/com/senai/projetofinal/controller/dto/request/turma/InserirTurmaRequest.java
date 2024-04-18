package com.senai.projetofinal.controller.dto.request.turma;

public record InserirTurmaRequest(
        String nome,
        Long docente,
        Long curso
) {
}
