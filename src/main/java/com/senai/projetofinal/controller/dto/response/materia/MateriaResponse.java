package com.senai.projetofinal.controller.dto.response.materia;

import com.senai.projetofinal.datasource.entity.CursoEntity;

public record MateriaResponse(
        Long id,
        String nome,
        CursoEntity curso
) {
}
