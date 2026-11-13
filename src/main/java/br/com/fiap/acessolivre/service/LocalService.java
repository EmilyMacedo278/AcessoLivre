package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.AtribuirResponsavelRequest;
import br.com.fiap.acessolivre.dto.LocalRequest;
import br.com.fiap.acessolivre.dto.LocalResponse;
import br.com.fiap.acessolivre.entity.Local;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.repository.LocalRepository;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalService {
    private final LocalRepository repository;
    private final UsuarioRepository usuarios;
    private final UsuarioAutenticadoService autenticado;

    @Transactional
    public LocalResponse cadastrar(LocalRequest request) {
        autenticado.exigirAdmin();
        Local local = new Local();
        local.setNome(request.nome());
        local.setTipo(request.tipo());
        local.setEndereco(request.endereco());
        return converterParaResponse(repository.save(local));
    }

    public Page<LocalResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(this::converterParaResponse);
    }

    public LocalResponse buscarPorId(Long id) {
        return converterParaResponse(buscarEntidade(id));
    }

    @Transactional
    public LocalResponse atualizar(Long id, LocalRequest request) {
        autenticado.exigirAdmin();
        Local local = buscarEntidade(id);
        local.setNome(request.nome());
        local.setTipo(request.tipo());
        local.setEndereco(request.endereco());
        return converterParaResponse(repository.save(local));
    }

    @Transactional
    public LocalResponse atribuirResponsavel(Long id, AtribuirResponsavelRequest request) {
        autenticado.exigirAdmin();
        Local local = buscarEntidade(id);
        Usuario responsavel = usuarios.findById(request.responsavelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuário responsável não encontrado: " + request.responsavelId()));

        // ADMIN mantém o perfil; um cidadão designado passa a ser RESPONSAVEL.
        if (responsavel.getPerfil() == Perfil.CIDADAO) {
            responsavel.setPerfil(Perfil.RESPONSAVEL);
            usuarios.save(responsavel);
        }

        local.setResponsavel(responsavel);
        return converterParaResponse(repository.save(local));
    }

    @Transactional
    public void excluir(Long id) {
        autenticado.exigirAdmin();
        repository.delete(buscarEntidade(id));
    }

    private Local buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Local não encontrado: " + id));
    }

    private LocalResponse converterParaResponse(Local local) {
        Long responsavelId = local.getResponsavel() != null
                ? local.getResponsavel().getId() : null;
        return new LocalResponse(local.getId(), local.getNome(), local.getTipo(),
                local.getEndereco(), responsavelId);
    }
}
