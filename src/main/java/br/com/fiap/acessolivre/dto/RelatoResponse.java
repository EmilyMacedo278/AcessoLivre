package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.CondicaoRelatada;
import java.time.LocalDateTime;
public record RelatoResponse(
    Long id, Long autorId, Long recursoId, String descricao, CondicaoRelatada condicao,
    LocalDateTime observadoEm, LocalDateTime enviadoEm
) {}
