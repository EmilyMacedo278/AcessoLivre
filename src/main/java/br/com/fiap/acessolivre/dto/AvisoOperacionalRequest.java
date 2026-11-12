package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.CondicaoOperacional;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public record AvisoOperacionalRequest(
    @NotNull(message = "O recurso é obrigatório") @Positive Long recursoId,
    @NotBlank(message = "A descrição é obrigatória") @Size(max = 500) String descricao,
    @NotNull(message = "A condição é obrigatória") CondicaoOperacional condicao,
    @NotNull(message = "A data de início é obrigatória") LocalDateTime inicioOcorrencia,
    LocalDateTime previsaoRetorno
) {}
