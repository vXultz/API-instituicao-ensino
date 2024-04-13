package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.InserirLoginRequest;
import com.senai.projetofinal.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CadastroController {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<String> novoLogin(
            @RequestBody InserirLoginRequest inserirLoginRequest
    ) {
        usuarioService.cadastraNovoLogin(inserirLoginRequest);

        return ResponseEntity.ok("Usuário Salvo!");
    }
}
