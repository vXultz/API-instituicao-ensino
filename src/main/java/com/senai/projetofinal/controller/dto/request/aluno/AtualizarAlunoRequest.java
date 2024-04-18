package com.senai.projetofinal.controller.dto.request.aluno;

public record AtualizarAlunoRequest(
        String nome,
        String dataNascimento,
        Long turma
) {
}
