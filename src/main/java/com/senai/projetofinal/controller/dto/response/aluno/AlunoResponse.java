package com.senai.projetofinal.controller.dto.response.aluno;

import com.senai.projetofinal.datasource.entity.TurmaEntity;

public record AlunoResponse(
        Long id,
        String nome,
        String dataNascimento,
        TurmaEntity turma
) {
}
