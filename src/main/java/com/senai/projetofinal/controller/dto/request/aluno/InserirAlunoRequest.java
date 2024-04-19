package com.senai.projetofinal.controller.dto.request.aluno;

public record InserirAlunoRequest(
        String nome,
        String dataNascimento,
        Long usuario,
        Long turma
) {
}
