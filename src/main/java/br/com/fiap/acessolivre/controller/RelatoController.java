package br.com.fiap.acessolivre.controller;

import br.com.fiap.acessolivre.dto.RelatoRequest;
import br.com.fiap.acessolivre.dto.RelatoResponse;
import br.com.fiap.acessolivre.service.RelatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/relatos")
@RequiredArgsConstructor
public class RelatoController {
    private final RelatoService service;

    @PostMapping
    public ResponseEntity<RelatoResponse> cadastrar(@Valid @RequestBody RelatoRequest request) {
        RelatoResponse criado = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/relatos/" + criado.id())).body(criado);
    }

    @GetMapping
    public Page<RelatoResponse> listar(@RequestParam(required = false) Long recursoId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.listar(recursoId, pageable);
    }

    @GetMapping("/{id}")
    public RelatoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public RelatoResponse atualizar(@PathVariable Long id, @Valid @RequestBody RelatoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
