package br.com.fiap.acessolivre.dto;

import jakarta.validation.constraints.*;
public record LoginRequest(
    @NotBlank(message = "O e-mail é obrigatório") @Email String email,
    @NotBlank(message = "A senha é obrigatória") String senha
) {}
