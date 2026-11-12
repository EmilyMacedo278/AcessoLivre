package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.Perfil;
public record AuthResponse(
    String token, String tipo, Long usuarioId, String nome, Perfil perfil
) {}
