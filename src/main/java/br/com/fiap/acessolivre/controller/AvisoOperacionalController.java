package br.com.fiap.acessolivre.controller;

import br.com.fiap.acessolivre.dto.AvisoOperacionalRequest;
import br.com.fiap.acessolivre.dto.AvisoOperacionalResponse;
import br.com.fiap.acessolivre.service.AvisoOperacionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/avisos-operacionais")
@RequiredArgsConstructor
public class AvisoOperacionalController {
    private final AvisoOperacionalService service;

    @PostMapping
    public ResponseEntity<AvisoOperacionalResponse> cadastrar(
            @Valid @RequestBody AvisoOperacionalRequest request) {
        AvisoOperacionalResponse criado = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/avisos-operacionais/" + criado.id())).body(criado);
    }

    @GetMapping
    public Page<AvisoOperacionalResponse> listar(@RequestParam(required = false) Long recursoId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.listar(recursoId, pageable);
    }

    @GetMapping("/{id}")
    public AvisoOperacionalResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AvisoOperacionalResponse atualizar(@PathVariable Long id,
            @Valid @RequestBody AvisoOperacionalRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
