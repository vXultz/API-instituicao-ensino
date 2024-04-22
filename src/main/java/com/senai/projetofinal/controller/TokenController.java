package com.senai.projetofinal.controller;

import com.senai.projetofinal.controller.dto.request.LoginRequest;
import com.senai.projetofinal.controller.dto.response.LoginResponse;
import com.senai.projetofinal.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final TokenService tokenService;

    private static long TEMPO_EXPIRACAO = 36000L;


    @PostMapping("/login")
    public ResponseEntity<?> gerarToken(
            @RequestBody LoginRequest loginRequest){
        try {
            LoginResponse response = tokenService.gerarToken(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
