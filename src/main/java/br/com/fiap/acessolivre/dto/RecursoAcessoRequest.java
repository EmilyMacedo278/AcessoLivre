package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.TipoRecurso;
import jakarta.validation.constraints.*;
public record RecursoAcessoRequest(
    @NotBlank(message = "O nome é obrigatório") @Size(max = 150) String nome,
    @NotNull(message = "O tipo é obrigatório") TipoRecurso tipo,
    @NotBlank(message = "A localização interna é obrigatória") @Size(max = 200) String localizacaoInterna,
    @NotNull(message = "O local é obrigatório") @Positive Long localId
) {}
