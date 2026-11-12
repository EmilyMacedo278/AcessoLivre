package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.TipoRecurso;
public record RecursoAcessoResponse(
    Long id, String nome, TipoRecurso tipo, String localizacaoInterna, Long localId
) {}
