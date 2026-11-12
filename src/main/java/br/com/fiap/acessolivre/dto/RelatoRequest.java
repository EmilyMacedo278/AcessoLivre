package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.CondicaoRelatada;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public record RelatoRequest(
    @NotNull(message = "O recurso é obrigatório") @Positive Long recursoId,
    @NotBlank(message = "A descrição é obrigatória") @Size(max = 500) String descricao,
    @NotNull(message = "A condição é obrigatória") CondicaoRelatada condicao,
    @NotNull(message = "A data da observação é obrigatória") LocalDateTime observadoEm
) {}
