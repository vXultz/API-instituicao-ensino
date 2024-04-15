package com.senai.projetofinal.controller.dto.request;

public record InserirLoginRequest(

        String nomeLogin,
        String senha,
        String nomePapel
) {
}
