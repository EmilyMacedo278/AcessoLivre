package br.com.fiap.acessolivre.dto;

import br.com.fiap.acessolivre.enums.Perfil;
public record UsuarioResponse(
    Long id, String nome, String email, Perfil perfil
) {}
