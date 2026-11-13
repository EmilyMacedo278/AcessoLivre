package br.com.fiap.acessolivre.evento;

// Dados mínimos, sem e-mail, nome ou outras informações pessoais.
public record RelatoCriadoEvento(
        Long relatoId,
        Long recursoId,
        String condicao
) {}
