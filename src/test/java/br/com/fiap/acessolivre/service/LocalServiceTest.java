package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.AtribuirResponsavelRequest;
import br.com.fiap.acessolivre.dto.LocalResponse;
import br.com.fiap.acessolivre.entity.Local;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.repository.LocalRepository;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalServiceTest {

    @Mock private LocalRepository locais;
    @Mock private UsuarioRepository usuarios;
    @Mock private UsuarioAutenticadoService autenticado;
    @InjectMocks private LocalService service;

    private Local local() {
        Local local = new Local();
        local.setId(10L);
        local.setNome("Estacao Central");
        local.setTipo("Estacao");
        local.setEndereco("Rua de teste");
        return local;
    }

    private Usuario usuario(Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(20L);
        usuario.setNome("Mariana");
        usuario.setPerfil(perfil);
        return usuario;
    }

    @Test
    void adminAtribuiCidadaEAlteraPerfilParaResponsavel() {
        Local local = local();
        Usuario responsavel = usuario(Perfil.CIDADAO);
        when(locais.findById(10L)).thenReturn(Optional.of(local));
        when(usuarios.findById(20L)).thenReturn(Optional.of(responsavel));
        when(locais.save(local)).thenReturn(local);

        LocalResponse resposta = service.atribuirResponsavel(10L, new AtribuirResponsavelRequest(20L));

        verify(autenticado).exigirAdmin();
        assertEquals(Perfil.RESPONSAVEL, responsavel.getPerfil());
        verify(usuarios).save(responsavel);
        assertEquals(20L, resposta.responsavelId());
    }

    @Test
    void adminPodeSerResponsavelSemPerderPerfilAdmin() {
        Local local = local();
        Usuario admin = usuario(Perfil.ADMIN);
        when(locais.findById(10L)).thenReturn(Optional.of(local));
        when(usuarios.findById(20L)).thenReturn(Optional.of(admin));
        when(locais.save(local)).thenReturn(local);

        LocalResponse resposta = service.atribuirResponsavel(10L, new AtribuirResponsavelRequest(20L));

        assertEquals(Perfil.ADMIN, admin.getPerfil());
        assertEquals(20L, resposta.responsavelId());
        verify(usuarios, never()).save(any(Usuario.class));
    }

    @Test
    void cidadaoNaoPodeAtribuirResponsavel() {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores"))
                .when(autenticado).exigirAdmin();

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> service.atribuirResponsavel(10L, new AtribuirResponsavelRequest(20L)));

        assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
        verifyNoInteractions(locais, usuarios);
    }
}
