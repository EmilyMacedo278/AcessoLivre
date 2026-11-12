package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.RecursoAcessoRequest;
import br.com.fiap.acessolivre.dto.RecursoAcessoResponse;
import br.com.fiap.acessolivre.entity.Local;
import br.com.fiap.acessolivre.entity.RecursoAcesso;
import br.com.fiap.acessolivre.repository.LocalRepository;
import br.com.fiap.acessolivre.repository.RecursoAcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecursoAcessoService {
    private final RecursoAcessoRepository repository;
    private final LocalRepository locais;
    private final UsuarioAutenticadoService autenticado;

    @Transactional
    public RecursoAcessoResponse cadastrar(RecursoAcessoRequest request) {
        Local local = localizarLocal(request.localId());
        autenticado.exigirResponsavelOuAdmin(local);
        RecursoAcesso recurso = new RecursoAcesso();
        preencher(recurso, request, local);
        return responder(repository.save(recurso));
    }

    public Page<RecursoAcessoResponse> listar(Long localId, Pageable pageable) {
        Page<RecursoAcesso> pagina = localId == null
                ? repository.findAll(pageable) : repository.findByLocalId(localId, pageable);
        return pagina.map(this::responder);
    }

    public RecursoAcessoResponse buscarPorId(Long id) {
        return responder(buscar(id));
    }

    @Transactional
    public RecursoAcessoResponse atualizar(Long id, RecursoAcessoRequest request) {
        RecursoAcesso recurso = buscar(id);
        Local novoLocal = localizarLocal(request.localId());
        autenticado.exigirResponsavelOuAdmin(recurso.getLocal());
        autenticado.exigirResponsavelOuAdmin(novoLocal);
        preencher(recurso, request, novoLocal);
        return responder(repository.save(recurso));
    }

    @Transactional
    public void excluir(Long id) {
        RecursoAcesso recurso = buscar(id);
        autenticado.exigirResponsavelOuAdmin(recurso.getLocal());
        try {
            repository.delete(recurso);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Recurso possui relatos ou avisos associados", ex);
        }
    }

    private RecursoAcesso buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso não encontrado: " + id));
    }

    private Local localizarLocal(Long id) {
        return locais.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Local não encontrado: " + id));
    }

    private void preencher(RecursoAcesso r, RecursoAcessoRequest dados, Local local) {
        r.setNome(dados.nome());
        r.setTipo(dados.tipo());
        r.setLocalizacaoInterna(dados.localizacaoInterna());
        r.setLocal(local);
    }

    private RecursoAcessoResponse responder(RecursoAcesso r) {
        return new RecursoAcessoResponse(r.getId(), r.getNome(), r.getTipo(),
                r.getLocalizacaoInterna(), r.getLocal().getId());
    }
}
