package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.AvisoOperacionalRequest;
import br.com.fiap.acessolivre.dto.AvisoOperacionalResponse;
import br.com.fiap.acessolivre.entity.AvisoOperacional;
import br.com.fiap.acessolivre.entity.RecursoAcesso;
import br.com.fiap.acessolivre.repository.AvisoOperacionalRepository;
import br.com.fiap.acessolivre.repository.RecursoAcessoRepository;
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
public class AvisoOperacionalService {
    private final AvisoOperacionalRepository repository;
    private final RecursoAcessoRepository recursos;
    private final UsuarioAutenticadoService autenticado;

    @Transactional
    public AvisoOperacionalResponse cadastrar(AvisoOperacionalRequest request) {
        RecursoAcesso recurso = buscarRecurso(request.recursoId());
        autenticado.exigirResponsavelOuAdmin(recurso.getLocal());
        validarPrevisao(request);
        AvisoOperacional aviso = new AvisoOperacional();
        aviso.setAutor(autenticado.atual()); // autor vem da autenticação
        preencher(aviso, request, recurso);
        return responder(repository.save(aviso));
    }

    public Page<AvisoOperacionalResponse> listar(Long recursoId, Pageable pageable) {
        Page<AvisoOperacional> pagina = recursoId == null
                ? repository.findAll(pageable) : repository.findByRecursoId(recursoId, pageable);
        return pagina.map(this::responder);
    }

    public AvisoOperacionalResponse buscarPorId(Long id) {
        return responder(buscar(id));
    }

    @Transactional
    public AvisoOperacionalResponse atualizar(Long id, AvisoOperacionalRequest request) {
        AvisoOperacional aviso = buscar(id);
        RecursoAcesso novoRecurso = buscarRecurso(request.recursoId());
        autenticado.exigirResponsavelOuAdmin(aviso.getRecurso().getLocal());
        autenticado.exigirResponsavelOuAdmin(novoRecurso.getLocal());
        validarPrevisao(request);
        preencher(aviso, request, novoRecurso);
        return responder(repository.save(aviso));
    }

    @Transactional
    public void excluir(Long id) {
        AvisoOperacional aviso = buscar(id);
        autenticado.exigirResponsavelOuAdmin(aviso.getRecurso().getLocal());
        repository.delete(aviso);
    }

    private void validarPrevisao(AvisoOperacionalRequest request) {
        if (request.previsaoRetorno() != null
                && request.previsaoRetorno().isBefore(request.inicioOcorrencia())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A previsão de retorno não pode ser anterior ao início");
        }
    }

    private AvisoOperacional buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso não encontrado: " + id));
    }

    private RecursoAcesso buscarRecurso(Long id) {
        return recursos.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso não encontrado: " + id));
    }

    private void preencher(AvisoOperacional aviso, AvisoOperacionalRequest request, RecursoAcesso recurso) {
        aviso.setRecurso(recurso);
        aviso.setDescricao(request.descricao());
        aviso.setCondicao(request.condicao());
        aviso.setInicioOcorrencia(request.inicioOcorrencia());
        aviso.setPrevisaoRetorno(request.previsaoRetorno());
    }

    private AvisoOperacionalResponse responder(AvisoOperacional a) {
        return new AvisoOperacionalResponse(a.getId(), a.getAutor().getId(), a.getRecurso().getId(),
                a.getDescricao(), a.getCondicao(), a.getInicioOcorrencia(),
                a.getPrevisaoRetorno(), a.getPublicadoEm());
    }
}
