package br.com.fiap.acessolivre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
@Entity
@Table(name = "locais")
@Data
@NoArgsConstructor
@ToString(exclude = "responsavel")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Local {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 80)
    private String tipo;

    @Column(nullable = false, length = 255)
    private String endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;
}

