package br.com.fiap.acessolivre.repository;

import br.com.fiap.acessolivre.entity.AvisoOperacional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoOperacionalRepository extends JpaRepository<AvisoOperacional, Long> {
    Page<AvisoOperacional> findByRecursoId(Long recursoId, Pageable pageable);
}
