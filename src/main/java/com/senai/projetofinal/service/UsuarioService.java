package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.InserirLoginRequest;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;
import com.senai.projetofinal.datasource.repository.PapelRepository;
import com.senai.projetofinal.datasource.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;

    public void cadastraNovoLogin(
            @RequestBody InserirLoginRequest inserirLoginRequest
    ) {
        boolean loginExiste = usuarioRepository.findByLogin(inserirLoginRequest.nomeLogin())
                .isPresent();

        if (loginExiste) {
            throw new RuntimeException("Nome de Login já existe");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setLogin(inserirLoginRequest.nomeLogin());
        usuario.setSenha(
                bCryptPasswordEncoder.encode(inserirLoginRequest.senha())
        );
        usuario.setPapel(
                papelRepository.findByNome(inserirLoginRequest.nomePapel())
                        .orElseThrow(() -> new RuntimeException("Papel inválido ou inexistente"))
        );

        usuarioRepository.save(usuario);
    }
}
