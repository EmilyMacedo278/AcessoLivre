package br.com.fiap.acessolivre.entity;

import br.com.fiap.acessolivre.enums.CondicaoRelatada;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
@Entity
@Table(name = "relatos")
@Data
@NoArgsConstructor
@ToString(exclude = {"autor", "recurso"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Relato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurso_id", nullable = false)
    private RecursoAcesso recurso;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicaoRelatada condicao;

    @Column(name = "observado_em", nullable = false)
    private LocalDateTime observadoEm;

    @Column(name = "enviado_em", nullable = false, updatable = false)
    private LocalDateTime enviadoEm;

    @PrePersist
    public void antesDeSalvar() {
        enviadoEm = LocalDateTime.now();
    }
}

