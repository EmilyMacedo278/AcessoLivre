package br.com.fiap.acessolivre.dto;

import jakarta.validation.constraints.*;
public record UsuarioAtualizacaoRequest(
    @NotBlank(message = "O nome é obrigatório") @Size(max = 120) String nome,
    @NotBlank(message = "O e-mail é obrigatório") @Email @Size(max = 150) String email
) {}
