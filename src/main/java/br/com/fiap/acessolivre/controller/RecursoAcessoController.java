package br.com.fiap.acessolivre.controller;

import br.com.fiap.acessolivre.dto.RecursoAcessoRequest;
import br.com.fiap.acessolivre.dto.RecursoAcessoResponse;
import br.com.fiap.acessolivre.service.RecursoAcessoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoAcessoController {
    private final RecursoAcessoService service;

    @PostMapping
    public ResponseEntity<RecursoAcessoResponse> cadastrar(@Valid @RequestBody RecursoAcessoRequest request) {
        RecursoAcessoResponse criado = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/recursos/" + criado.id())).body(criado);
    }

    @GetMapping
    public Page<RecursoAcessoResponse> listar(@RequestParam(required = false) Long localId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.listar(localId, pageable);
    }

    @GetMapping("/{id}")
    public RecursoAcessoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public RecursoAcessoResponse atualizar(@PathVariable Long id,
            @Valid @RequestBody RecursoAcessoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
