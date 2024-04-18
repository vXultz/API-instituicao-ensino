package com.senai.projetofinal.controller.dto.request.turma;

public record AtualizarTurmaRequest(
        String nome,
        Long docente,
        Long curso
) {
}
