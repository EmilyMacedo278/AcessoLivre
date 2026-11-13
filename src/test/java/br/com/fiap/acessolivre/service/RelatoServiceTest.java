package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.RelatoRequest;
import br.com.fiap.acessolivre.entity.RecursoAcesso;
import br.com.fiap.acessolivre.entity.Relato;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.CondicaoRelatada;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.evento.RelatoCriadoEvento;
import br.com.fiap.acessolivre.repository.RecursoAcessoRepository;
import br.com.fiap.acessolivre.repository.RelatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatoServiceTest {

    @Mock private RelatoRepository relatos;
    @Mock private RecursoAcessoRepository recursos;
    @Mock private UsuarioAutenticadoService autenticado;
    @Mock private ApplicationEventPublisher eventos;
    @InjectMocks private RelatoService service;

    @Test
    void impedeRelatoComObservacaoNoFuturo() {
        Usuario autor = new Usuario();
        autor.setId(2L);
        when(autenticado.atual()).thenReturn(autor);

        RelatoRequest request = new RelatoRequest(5L, "Teste", CondicaoRelatada.COM_PROBLEMA,
                LocalDateTime.now().plusDays(1));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(request));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verifyNoInteractions(relatos, recursos, eventos);
    }

    @Test
    void publicacaoDeRelatoDisparaEventoComIdsCorretos() {
        Usuario autor = new Usuario();
        autor.setId(2L);
        RecursoAcesso recurso = new RecursoAcesso();
        recurso.setId(5L);
        when(autenticado.atual()).thenReturn(autor);
        when(recursos.findById(5L)).thenReturn(Optional.of(recurso));
        when(relatos.save(any(Relato.class))).thenAnswer(chamada -> {
            Relato salvo = chamada.getArgument(0);
            salvo.setId(11L);
            salvo.antesDeSalvar();
            return salvo;
        });

        RelatoRequest request = new RelatoRequest(5L, "Elevador parado",
                CondicaoRelatada.COM_PROBLEMA, LocalDateTime.now().minusHours(2));
        service.cadastrar(request);

        ArgumentCaptor<RelatoCriadoEvento> captor = ArgumentCaptor.forClass(RelatoCriadoEvento.class);
        verify(eventos).publishEvent(captor.capture());
        RelatoCriadoEvento publicado = captor.getValue();
        assertEquals(11L, publicado.relatoId());
        assertEquals(5L, publicado.recursoId());
        assertEquals("COM_PROBLEMA", publicado.condicao());
    }

    @Test
    void impedeQueOutroCidadaoEditeRelatoDeTerceiro() {
        Usuario autor = new Usuario();
        autor.setId(2L);
        Usuario intruso = new Usuario();
        intruso.setId(3L);
        intruso.setPerfil(Perfil.CIDADAO);
        Relato existente = new Relato();
        existente.setId(11L);
        existente.setAutor(autor);
        when(relatos.findById(11L)).thenReturn(Optional.of(existente));
        when(autenticado.atual()).thenReturn(intruso);

        RelatoRequest request = new RelatoRequest(5L, "Alteracao indevida",
                CondicaoRelatada.FUNCIONANDO, LocalDateTime.now().minusHours(1));
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.atualizar(11L, request));

        assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
        verify(relatos, never()).save(any(Relato.class));
        verifyNoInteractions(eventos);
    }
}
