package br.com.fiap.acessolivre.dto;

import java.time.Instant;

public record ResumoRecursoResponse(
        Long recursoId,
        String recurso,
        String local,
        int relatosAnalisados,
        int avisosAnalisados,
        String resumo,
        Instant geradoEm
) {}
