package br.com.fiap.acessolivre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos_relato_processados")
public class EventoRelatoProcessado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "relato_id", nullable = false, unique = true)
    private Long relatoId;

    @Column(name = "recurso_id", nullable = false)
    private Long recursoId;

    @Column(nullable = false, length = 30)
    private String condicao;

    @Column(name = "processado_em", nullable = false)
    private LocalDateTime processadoEm;

    protected EventoRelatoProcessado() {}

    public EventoRelatoProcessado(Long relatoId, Long recursoId, String condicao) {
        this.relatoId = relatoId;
        this.recursoId = recursoId;
        this.condicao = condicao;
        this.processadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getRelatoId() { return relatoId; }
    public Long getRecursoId() { return recursoId; }
    public String getCondicao() { return condicao; }
    public LocalDateTime getProcessadoEm() { return processadoEm; }
}
