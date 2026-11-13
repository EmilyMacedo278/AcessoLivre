package br.com.fiap.acessolivre;

import br.com.fiap.acessolivre.security.JwtConfig;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes leves de configuração: não iniciam Spring, MySQL nem RabbitMQ.
 * Substitui o antigo contextLoads que exigia serviços externos na pipeline.
 */
class AcessoLivreApplicationTests {

    private final JwtConfig config = new JwtConfig();

    @Test
    void rejeitaChaveJwtQueNaoEBase64() {
        assertThrows(IllegalStateException.class, () -> config.jwtSigningKey("!nao-e-base64!"));
    }

    @Test
    void rejeitaChaveJwtComMenosDe32Bytes() {
        String curta = Base64.getEncoder().encodeToString(new byte[8]);
        assertThrows(IllegalStateException.class, () -> config.jwtSigningKey(curta));
    }
}
