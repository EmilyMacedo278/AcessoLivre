package br.com.fiap.acessolivre.repository;

import br.com.fiap.acessolivre.entity.Relato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatoRepository extends JpaRepository<Relato, Long> {
    Page<Relato> findByRecursoId(Long recursoId, Pageable pageable);
}
