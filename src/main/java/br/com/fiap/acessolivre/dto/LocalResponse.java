package br.com.fiap.acessolivre.dto;


public record LocalResponse(
    Long id, String nome, String tipo, String endereco, Long responsavelId
) {}
