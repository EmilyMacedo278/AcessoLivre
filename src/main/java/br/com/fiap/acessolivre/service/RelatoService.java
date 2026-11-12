package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.RelatoRequest;
import br.com.fiap.acessolivre.dto.RelatoResponse;
import br.com.fiap.acessolivre.entity.Relato;
import br.com.fiap.acessolivre.entity.RecursoAcesso;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.repository.RelatoRepository;
import br.com.fiap.acessolivre.repository.RecursoAcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RelatoService {
    private final RelatoRepository repository;
    private final RecursoAcessoRepository recursos;
    private final UsuarioAutenticadoService autenticado;

    @Transactional
    public RelatoResponse cadastrar(RelatoRequest request) {
        Usuario autor = autenticado.atual();
        validarData(request.observadoEm());
        Relato relato = new Relato();
        relato.setAutor(autor); // nunca confiar em autorId informado pelo cliente
        preencher(relato, request);
        return responder(repository.save(relato));
    }

    public Page<RelatoResponse> listar(Long recursoId, Pageable pageable) {
        Page<Relato> pagina = recursoId == null
                ? repository.findAll(pageable) : repository.findByRecursoId(recursoId, pageable);
        return pagina.map(this::responder);
    }

    public RelatoResponse buscarPorId(Long id) {
        return responder(buscar(id));
    }

    @Transactional
    public RelatoResponse atualizar(Long id, RelatoRequest request) {
        Relato relato = buscar(id);
        exigirAutorOuAdmin(relato);
        validarData(request.observadoEm());
        preencher(relato, request);
        return responder(repository.save(relato));
    }

    @Transactional
    public void excluir(Long id) {
        Relato relato = buscar(id);
        exigirAutorOuAdmin(relato);
        repository.delete(relato);
    }

    private void validarData(LocalDateTime observadoEm) {
        // Usa o horário local do servidor; ao publicar em Azure, padronizar como UTC/OffsetDateTime.
        if (observadoEm.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A observação não pode estar no futuro");
        }
    }

    private void exigirAutorOuAdmin(Relato relato) {
        Usuario atual = autenticado.atual();
        if (atual.getPerfil() != Perfil.ADMIN && !atual.getId().equals(relato.getAutor().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o autor pode alterar este relato");
        }
    }

    private Relato buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Relato não encontrado: " + id));
    }

    private void preencher(Relato relato, RelatoRequest request) {
        RecursoAcesso recurso = recursos.findById(request.recursoId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso não encontrado: " + request.recursoId()));
        relato.setRecurso(recurso);
        relato.setDescricao(request.descricao());
        relato.setCondicao(request.condicao());
        relato.setObservadoEm(request.observadoEm());
    }

    private RelatoResponse responder(Relato r) {
        return new RelatoResponse(r.getId(), r.getAutor().getId(), r.getRecurso().getId(),
                r.getDescricao(), r.getCondicao(), r.getObservadoEm(), r.getEnviadoEm());
    }
}
