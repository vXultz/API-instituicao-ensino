package com.senai.projetofinal.infra.exception.error;

public class SecurityException extends RuntimeException {
    public SecurityException() {
    }

    public SecurityException(String message) {
        super(message);
    }
}
