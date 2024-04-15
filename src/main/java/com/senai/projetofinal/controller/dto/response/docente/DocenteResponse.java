package com.senai.projetofinal.controller.dto.response.docente;

import com.senai.projetofinal.datasource.entity.UsuarioEntity;

public record DocenteResponse(
        Long id,
        String nome,
        UsuarioEntity usuario
) {
}
