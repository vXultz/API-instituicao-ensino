package com.senai.projetofinal.datasource.entity;

public enum PapelEnum {
    ADMIN("admin"),
    PEDAGOGICO("pedagogico"),
    RECRUITER("recruiter"),
    PROFESSOR("professor"),
    ALUNO("aluno");

    private final String nome;

    PapelEnum(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() {
        return nome;
    }
}