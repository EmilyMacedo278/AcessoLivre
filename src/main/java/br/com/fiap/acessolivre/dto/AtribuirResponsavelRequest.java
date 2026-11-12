package br.com.fiap.acessolivre.dto;

import jakarta.validation.constraints.*;
public record AtribuirResponsavelRequest(
    @NotNull(message = "O responsável é obrigatório") @Positive Long responsavelId
) {}
