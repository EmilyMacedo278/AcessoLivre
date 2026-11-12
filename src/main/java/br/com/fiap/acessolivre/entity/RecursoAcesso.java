package br.com.fiap.acessolivre.entity;

import br.com.fiap.acessolivre.enums.TipoRecurso;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
@Entity
@Table(name = "recursos_acesso")
@Data
@NoArgsConstructor
@ToString(exclude = "local")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecursoAcesso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoRecurso tipo;

    @Column(name = "localizacao_interna", nullable = false, length = 200)
    private String localizacaoInterna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;
}

