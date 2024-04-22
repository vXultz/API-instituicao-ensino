package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.InserirLoginRequest;
import com.senai.projetofinal.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CadastroController {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<String> novoLogin(
            @RequestBody InserirLoginRequest inserirLoginRequest,
            @RequestHeader("Authorization") String token) {
        try {
            usuarioService.cadastraNovoLogin(inserirLoginRequest, token.substring(7));
            return ResponseEntity.ok("Usuário Salvo!");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
