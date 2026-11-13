package br.com.fiap.acessolivre.repository;

import br.com.fiap.acessolivre.entity.EventoRelatoProcessado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRelatoProcessadoRepository extends JpaRepository<EventoRelatoProcessado, Long> {
    boolean existsByRelatoId(Long relatoId);
}
