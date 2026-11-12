package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.CondicaoOperacional;
import java.time.LocalDateTime;
public record AvisoOperacionalResponse(
    Long id, Long autorId, Long recursoId, String descricao, CondicaoOperacional condicao,
    LocalDateTime inicioOcorrencia, LocalDateTime previsaoRetorno, LocalDateTime publicadoEm
) {}
