package br.com.fiap.acessolivre;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(
        named = "RUN_INTEGRATION_TESTS",
        matches = "true")
@SpringBootTest
class AcessoLivreApplicationTests {

    @Test
    void contextLoads() {
    }

}
