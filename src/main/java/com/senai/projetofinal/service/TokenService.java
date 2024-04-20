package com.senai.projetofinal.service;

import com.senai.projetofinal.controller.dto.request.LoginRequest;
import com.senai.projetofinal.controller.dto.response.LoginResponse;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;
import com.senai.projetofinal.datasource.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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
    private final JwtDecoder jwtDecoder;
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
                            log.error("Usuário incorreto");
                            return new BadCredentialsException("Usuário incorreto");
                        }
                );


        if (loginRequest.login() == null || loginRequest.login().isBlank()) {
            throw new IllegalArgumentException("Login não pode ser nulo ou vazio");
        }

        if (loginRequest.senha() == null || loginRequest.senha().isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }

        if (!usuarioEntity.senhaValida(loginRequest, bCryptPasswordEncoder)){
            log.error("Senha incorreta");
            throw new BadCredentialsException("Senha incorreta");
        }

        Instant now = Instant.now();

        String scope = String.valueOf(usuarioEntity.getPapel().getNome());

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

    public String buscaCampo(String token, String claim) {
        return jwtDecoder
                .decode(token)
                .getClaims()
                .get(claim)
                .toString();
    }
}
