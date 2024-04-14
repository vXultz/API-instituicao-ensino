package com.senai.projetofinal.service;

import com.senai.projetofinal.datasource.entity.DocenteEntity;
import com.senai.projetofinal.datasource.repository.DocenteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocenteService {
    
    private final DocenteRepository repository;

    public DocenteService(DocenteRepository docenteRepository) {
        this.repository = docenteRepository;
    }

    public List<DocenteEntity> listarTodos() {
        log.info("todos os docentees listados");
        return repository.findAll();
    }

    public DocenteEntity buscarPorId(Long id) {
        log.info("docente com id {} buscado", id);
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Docente não encontrado"));
    }

    public DocenteEntity salvar(DocenteEntity docente) {
        log.info("salvando docente com o nome {}", docente.getNome());
        docente.setId(null);
        return repository.save(docente);
    }

    public void removerPorId(Long id) {
        log.info("removendo docente com o id {}", id);
        repository.deleteById(id);
    }

    public DocenteEntity atualizar(DocenteEntity docente, Long id) {
        DocenteEntity entity = buscarPorId(id);
        log.info("atualizando docente com o id {}", docente.getId());
        entity.setNome(docente.getNome());
        return repository.save(entity);
    }
}
