package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.InserirLoginRequest;
import com.senai.projetofinal.datasource.entity.PapelEnum;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;
import com.senai.projetofinal.datasource.repository.PapelRepository;
import com.senai.projetofinal.datasource.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final TokenService tokenService;

    public void cadastraNovoLogin(
            @RequestBody InserirLoginRequest inserirLoginRequest,
            @RequestHeader("Authorization") String token) {

        String role = tokenService.buscaCampo(token, "scope");

        if (!"admin".equals(role)) {
            log.error("Usuário não autorizado: {}", role);
            throw new SecurityException("Apenas um admin pode cadastrar novos usuários");
        }

        if (inserirLoginRequest.nomeLogin() == null || inserirLoginRequest.nomeLogin().isBlank()) {
            log.error("Nome não pode ser nulo ou vazio");
            throw new IllegalArgumentException("Login não pode ser nulo ou vazio");
        }

        if (inserirLoginRequest.senha() == null || inserirLoginRequest.senha().isBlank()) {
            log.error("Senha não pode ser nula ou vazia");
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }

        boolean loginExiste = usuarioRepository.findByLogin(inserirLoginRequest.nomeLogin())
                .isPresent();

        if (loginExiste) {
            log.error("Nome de Login já existe: {}", inserirLoginRequest.nomeLogin());
            throw new RuntimeException("Nome de Login já existe");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setLogin(inserirLoginRequest.nomeLogin());
        usuario.setSenha(
                bCryptPasswordEncoder.encode(inserirLoginRequest.senha())
        );
        usuario.setPapel(
                papelRepository.findByNome(PapelEnum.valueOf(inserirLoginRequest.nomePapel().toUpperCase()))
                        .orElseThrow(() -> new RuntimeException("Papel inválido ou inexistente"))
        );

        usuarioRepository.save(usuario);
    }

    public UsuarioEntity buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
