package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.LoginRequest;
import com.senai.projetofinal.controller.dto.response.LoginResponse;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;
import com.senai.projetofinal.datasource.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;

    private static long TEMPO_EXPIRACAO = 36000L;

    public LoginResponse gerarToken(
            @RequestBody LoginRequest loginRequest
    ){
        UsuarioEntity usuarioEntity = usuarioRepository
                .findByLogin(loginRequest.login())
                .orElseThrow(
                        () ->{
                            log.error("Erro, usuário não existe");
                            return new RuntimeException("Erro, usuário não existe");
                        }
                );

        if (!usuarioEntity.senhaValida(loginRequest, bCryptPasswordEncoder)){
            log.error("Erro, senha incorreta");
            throw new RuntimeException("Erro, senha incorreta");
        }

        Instant now = Instant.now();

        String scope = usuarioEntity.getPapel().getNome();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("sistema-escolar")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(TEMPO_EXPIRACAO))
                .subject(usuarioEntity.getId().toString())
                .claim("scope", scope)
                .build();

        var valorJWT = jwtEncoder.encode(
                        JwtEncoderParameters.from(claims)
                )
                .getTokenValue();

        return new LoginResponse(valorJWT, TEMPO_EXPIRACAO);
    }
}
