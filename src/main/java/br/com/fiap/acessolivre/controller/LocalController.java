package br.com.fiap.acessolivre.controller;

import br.com.fiap.acessolivre.dto.LocalRequest;
import br.com.fiap.acessolivre.dto.LocalResponse;
import br.com.fiap.acessolivre.service.LocalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
@RestController
@RequestMapping("/api/locais")
@RequiredArgsConstructor
public class LocalController {
    private final LocalService service;

    @PostMapping
    public ResponseEntity<LocalResponse> cadastrar(@Valid @RequestBody LocalRequest request) {
        LocalResponse criado = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/locais/" + criado.id())).body(criado);
    }

    @GetMapping
    public Page<LocalResponse> listar(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody LocalRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

