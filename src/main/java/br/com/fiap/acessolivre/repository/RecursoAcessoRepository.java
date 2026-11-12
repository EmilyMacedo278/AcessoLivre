package br.com.fiap.acessolivre.repository;

import br.com.fiap.acessolivre.entity.RecursoAcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecursoAcessoRepository extends JpaRepository<RecursoAcesso, Long> {
    Page<RecursoAcesso> findByLocalId(Long localId, Pageable pageable);
}
