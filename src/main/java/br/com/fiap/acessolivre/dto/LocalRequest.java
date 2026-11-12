package br.com.fiap.acessolivre.dto;

import jakarta.validation.constraints.*;
public record LocalRequest(
    @NotBlank(message = "O nome é obrigatório") @Size(max = 150) String nome,
    @NotBlank(message = "O tipo é obrigatório") @Size(max = 80) String tipo,
    @NotBlank(message = "O endereço é obrigatório") @Size(max = 255) String endereco
) {}
