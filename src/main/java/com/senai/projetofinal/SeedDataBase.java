package com.senai.projetofinal;

import com.senai.projetofinal.datasource.entity.PapelEntity;
import com.senai.projetofinal.datasource.entity.PapelEnum;
import com.senai.projetofinal.datasource.entity.UsuarioEntity;
import com.senai.projetofinal.datasource.repository.PapelRepository;
import com.senai.projetofinal.datasource.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
class SeedDatabase {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    SeedDatabase(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PapelRepository papelRepository) {
        return args -> {

            if (papelRepository.count() == 0) {
                for (PapelEnum name : PapelEnum.values()) {
                    if (papelRepository.findByNome(name).isEmpty()) {
                        PapelEntity newPapel = new PapelEntity();
                        newPapel.setNome(name);
                        papelRepository.save(newPapel);
                    }
                }
            }

            if (usuarioRepository.count() == 0) {
                UsuarioEntity admin = new UsuarioEntity();
                admin.setLogin("admin");
                admin.setSenha(bCryptPasswordEncoder.encode("1234"));
                PapelEntity adminPapel = papelRepository.findByNome(PapelEnum.ADMIN).orElseThrow();
                admin.setPapel(adminPapel);
                usuarioRepository.save(admin);

                UsuarioEntity pedagogico = new UsuarioEntity();
                pedagogico.setLogin("pedagogico");
                pedagogico.setSenha(bCryptPasswordEncoder.encode("1234"));
                PapelEntity pedagogigoPapel = papelRepository.findByNome(PapelEnum.PEDAGOGICO).orElseThrow();
                pedagogico.setPapel(pedagogigoPapel);
                usuarioRepository.save(pedagogico);

                UsuarioEntity recruiter = new UsuarioEntity();
                recruiter.setLogin("recruiter");
                recruiter.setSenha(bCryptPasswordEncoder.encode("1234"));
                PapelEntity recruiterPapel = papelRepository.findByNome(PapelEnum.RECRUITER).orElseThrow();
                recruiter.setPapel(recruiterPapel);
                usuarioRepository.save(recruiter);

                UsuarioEntity professor = new UsuarioEntity();
                professor.setLogin("professor");
                professor.setSenha(bCryptPasswordEncoder.encode("1234"));
                PapelEntity professorPapel = papelRepository.findByNome(PapelEnum.PROFESSOR).orElseThrow();
                professor.setPapel(professorPapel);
                usuarioRepository.save(professor);

                UsuarioEntity aluno = new UsuarioEntity();
                aluno.setLogin("aluno");
                aluno.setSenha(bCryptPasswordEncoder.encode("1234"));
                PapelEntity alunoPapel = papelRepository.findByNome(PapelEnum.ALUNO).orElseThrow();
                aluno.setPapel(alunoPapel);
                usuarioRepository.save(aluno);
            }
        };
    }
}
