package com.senai.projetofinal.controller.dto.request.nota;

public record InserirNotaRequest(
        Long aluno,
        Long docente,
        Long materia,
        String valor
) {
}
