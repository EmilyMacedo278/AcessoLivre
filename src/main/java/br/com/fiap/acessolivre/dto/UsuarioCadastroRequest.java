package br.com.fiap.acessolivre.dto;

import jakarta.validation.constraints.*;
public record UsuarioCadastroRequest(
    @NotBlank(message = "O nome é obrigatório") @Size(max = 120) String nome,
    @NotBlank(message = "O e-mail é obrigatório") @Email @Size(max = 150) String email,
    @NotBlank(message = "A senha é obrigatória") @Size(min = 8, max = 72) String senha
) {}
