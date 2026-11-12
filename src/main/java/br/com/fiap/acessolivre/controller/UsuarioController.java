package br.com.fiap.acessolivre.controller;

import br.com.fiap.acessolivre.dto.UsuarioAtualizacaoRequest;
import br.com.fiap.acessolivre.dto.UsuarioCadastroRequest;
import br.com.fiap.acessolivre.dto.UsuarioResponse;
import br.com.fiap.acessolivre.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    // A liberação pública deste endpoint só será feita na futura SecurityConfig/JWT.
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioCadastroRequest request) {
        UsuarioResponse criado = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + criado.id())).body(criado);
    }

    @GetMapping
    public Page<UsuarioResponse> listar(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id,
                                    @Valid @RequestBody UsuarioAtualizacaoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
