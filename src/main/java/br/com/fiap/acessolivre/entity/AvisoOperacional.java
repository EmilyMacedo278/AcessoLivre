package br.com.fiap.acessolivre.entity;

import br.com.fiap.acessolivre.enums.CondicaoOperacional;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
@Entity
@Table(name = "avisos_operacionais")
@Data
@NoArgsConstructor
@ToString(exclude = {"autor", "recurso"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AvisoOperacional {
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
    private CondicaoOperacional condicao;

    @Column(name = "inicio_ocorrencia", nullable = false)
    private LocalDateTime inicioOcorrencia;

    @Column(name = "previsao_retorno")
    private LocalDateTime previsaoRetorno;

    @Column(name = "publicado_em", nullable = false, updatable = false)
    private LocalDateTime publicadoEm;

    @PrePersist
    public void antesDeSalvar() {
        publicadoEm = LocalDateTime.now();
    }
}

