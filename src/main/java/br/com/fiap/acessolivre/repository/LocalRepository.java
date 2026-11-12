package br.com.fiap.acessolivre.repository;

import br.com.fiap.acessolivre.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LocalRepository extends JpaRepository<Local, Long> {
}
