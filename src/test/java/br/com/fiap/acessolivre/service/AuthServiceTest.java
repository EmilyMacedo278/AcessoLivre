
package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.AuthResponse;
import br.com.fiap.acessolivre.dto.LoginRequest;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import br.com.fiap.acessolivre.security.JwtConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarios;

    private AuthService service;
    private JwtDecoder decoder;
    private PasswordEncoder encoder;

    @BeforeEach
    void preparar() {
        JwtConfig configuracao = new JwtConfig();

        // Chave exclusiva para os testes.
        // Nao depende de .env ou de credenciais reais.
        byte[] chaveDeTeste = new byte[48];

        for (int i = 0; i < chaveDeTeste.length; i++) {
            chaveDeTeste[i] = (byte) (i + 1);
        }

        SecretKey chave = configuracao.jwtSigningKey(
                Base64.getEncoder().encodeToString(chaveDeTeste)
        );

        encoder = configuracao.passwordEncoder();
        decoder = configuracao.jwtDecoder(chave);

        service = new AuthService(
                usuarios,
                encoder,
                configuracao.jwtEncoder(chave)
        );
    }

    private Usuario usuario(Long id, String email) {
        Usuario usuario = new Usuario();

        usuario.setId(id);
        usuario.setNome("Ana Teste");
        usuario.setEmail(email);
        usuario.setSenhaHash(
                encoder.encode("SenhaTeste123!")
        );
        usuario.setPerfil(Perfil.ADMIN);

        return usuario;
    }

    @Test
    void loginGeraJwtComIdImutavelNoSubject() {
        when(usuarios.findByEmailIgnoreCase("ana@teste.com"))
                .thenReturn(
                        Optional.of(
                                usuario(7L, "ana@teste.com")
                        )
                );

        AuthResponse resposta = service.login(
                new LoginRequest(
                        "ANA@TESTE.COM",
                        "SenhaTeste123!"
                )
        );

        var jwt = decoder.decode(resposta.token());

        assertEquals(7L, resposta.usuarioId());
        assertEquals(Perfil.ADMIN, resposta.perfil());
        assertEquals("7", jwt.getSubject());

        // Lemos o emissor como texto, sem converter para URL.
        assertEquals(
                JwtConfig.ISSUER,
                jwt.getClaimAsString("iss")
        );
    }

    @Test
    void alteracaoDeEmailNaoAlteraIdentidadeDeNovosTokens() {
        Usuario usuario = usuario(
                7L,
                "ana@teste.com"
        );

        when(usuarios.findByEmailIgnoreCase("ana@teste.com"))
                .thenReturn(Optional.of(usuario));

        String antes = service.login(
                new LoginRequest(
                        "ana@teste.com",
                        "SenhaTeste123!"
                )
        ).token();

        usuario.setEmail("ana.novo@teste.com");

        when(usuarios.findByEmailIgnoreCase("ana.novo@teste.com"))
                .thenReturn(Optional.of(usuario));

        String depois = service.login(
                new LoginRequest(
                        "ana.novo@teste.com",
                        "SenhaTeste123!"
                )
        ).token();

        assertEquals(
                "7",
                decoder.decode(antes).getSubject()
        );

        assertEquals(
                "7",
                decoder.decode(depois).getSubject()
        );
    }

    @Test
    void recusaSenhaIncorretaCom401() {
        when(usuarios.findByEmailIgnoreCase("ana@teste.com"))
                .thenReturn(
                        Optional.of(
                                usuario(7L, "ana@teste.com")
                        )
                );

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> service.login(
                        new LoginRequest(
                                "ana@teste.com",
                                "senha-errada"
                        )
                )
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                erro.getStatusCode()
        );
    }
}
