package br.com.fiap.acessolivre.security;

import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastro", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/locais/**", "/api/recursos/**",
                        "/api/relatos/**", "/api/avisos-operacionais/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/locais/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/locais/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/locais/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(UsuarioRepository usuarios) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // A role vem do BANCO a cada requisição; a claim antiga pode estar desatualizada.
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            final Long usuarioId;
            try {
                usuarioId = Long.valueOf(jwt.getSubject());
            } catch (NumberFormatException ex) {
                throw new BadCredentialsException("Identidade do token inválida", ex);
            }
            Usuario usuario = usuarios.findById(usuarioId)
                    .orElseThrow(() -> new BadCredentialsException("Usuário do token não encontrado"));
            return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()));
        });
        return converter;
    }
}
