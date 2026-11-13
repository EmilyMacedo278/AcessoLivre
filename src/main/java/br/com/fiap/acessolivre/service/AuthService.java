package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.AuthResponse;
import br.com.fiap.acessolivre.dto.LoginRequest;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import br.com.fiap.acessolivre.security.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;

    public AuthResponse login(LoginRequest request) {
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarios.findByEmailIgnoreCase(email)
                .orElseThrow(this::credenciaisInvalidas);
        if (!encoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw credenciaisInvalidas();
        }

        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(JwtConfig.ISSUER)
                .subject(usuario.getId().toString())
                .issuedAt(agora)
                .expiresAt(agora.plus(60, ChronoUnit.MINUTES))
                .claim("perfil", usuario.getPerfil().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AuthResponse(token, "Bearer", usuario.getId(), usuario.getNome(), usuario.getPerfil());
    }

    private ResponseStatusException credenciaisInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
    }
}
