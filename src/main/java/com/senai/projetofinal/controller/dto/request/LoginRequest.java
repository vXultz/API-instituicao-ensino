package com.senai.projetofinal.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoginRequest (
        String login,
        String senha,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate localDate,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime localDateTime
){
}
