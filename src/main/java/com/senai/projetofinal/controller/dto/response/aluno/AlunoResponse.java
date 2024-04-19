package com.senai.projetofinal.controller.dto.response.aluno;

import com.senai.projetofinal.datasource.entity.TurmaEntity;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;

public record AlunoResponse(
        Long id,
        String nome,
        String dataNascimento,
        UsuarioEntity usuario,
        TurmaEntity turma
) {
}
