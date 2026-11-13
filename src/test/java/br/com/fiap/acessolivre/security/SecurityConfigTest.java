package br.com.fiap.acessolivre.security;

import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private UsuarioRepository usuarios;

    private JwtAuthenticationConverter converter;

    @BeforeEach
    void preparar() {
        converter = new SecurityConfig().jwtAuthenticationConverter(usuarios);
    }

    private Jwt tokenDe(Long usuarioId, String perfilAntigo) {
        return Jwt.withTokenValue("jwt-de-teste")
                .header("alg", "HS256")
                .claim("sub", usuarioId.toString())
                .claim("perfil", perfilAntigo)
                .build();
    }

    @Test
    void usaPerfilAtualDoBancoMesmoQuandoTokenDizAdmin() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setPerfil(Perfil.CIDADAO);
        when(usuarios.findById(7L)).thenReturn(Optional.of(usuario));

        var autenticacao = converter.convert(tokenDe(7L, "ADMIN"));

        assertNotNull(autenticacao);
        assertTrue(autenticacao.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CIDADAO")));
        assertFalse(autenticacao.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void reconheceAdminPeloPerfilAtualDoBanco() {
        Usuario usuario = new Usuario();
        usuario.setId(8L);
        usuario.setPerfil(Perfil.ADMIN);
        when(usuarios.findById(8L)).thenReturn(Optional.of(usuario));

        var autenticacao = converter.convert(tokenDe(8L, "CIDADAO"));

        assertNotNull(autenticacao);
        assertTrue(autenticacao.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void rejeitaTokenDeUsuarioQueNaoExisteMais() {
        when(usuarios.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> converter.convert(tokenDe(99L, "ADMIN")));
    }
}
