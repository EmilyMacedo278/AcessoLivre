package br.com.fiap.acessolivre.controller;

import br.com.fiap.acessolivre.dto.ResumoRecursoResponse;
import br.com.fiap.acessolivre.service.ResumoIaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class ResumoIaController {

    private final ResumoIaService service;

    // A autenticacao JWT ja e exigida por SecurityConfig.anyRequest().authenticated().
    @PostMapping("/recursos/{recursoId}/resumo")
    public ResumoRecursoResponse resumir(@PathVariable Long recursoId) {
        return service.resumir(recursoId);
    }
}
