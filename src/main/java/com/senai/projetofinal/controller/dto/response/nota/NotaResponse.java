package com.senai.projetofinal.controller.dto.response.nota;

import com.senai.projetofinal.datasource.entity.AlunoEntity;
import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.datasource.entity.MateriaEntity;

public record NotaResponse(
        Long id,
        AlunoEntity aluno,
        DocenteEntity docente,
        MateriaEntity materia,
        String valor
) {
}
