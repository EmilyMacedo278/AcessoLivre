package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.dto.UsuarioAtualizacaoRequest;
import br.com.fiap.acessolivre.dto.UsuarioCadastroRequest;
import br.com.fiap.acessolivre.dto.UsuarioResponse;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {
    private final UsuarioRepository repository;
    private final UsuarioAutenticadoService autenticado;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public UsuarioResponse cadastrar(UsuarioCadastroRequest request) {
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().strip());
        usuario.setEmail(email);
        usuario.setSenhaHash(encoder.encode(request.senha()));
        usuario.setPerfil(Perfil.CIDADAO); // Cadastro público nunca escolhe perfil.
        return responder(repository.save(usuario));
    }

    public Page<UsuarioResponse> listar(Pageable pageable) {
        autenticado.exigirAdmin();
        return repository.findAll(pageable).map(this::responder);
    }

    public UsuarioResponse buscarPorId(Long id) {
        autenticado.exigirProprioOuAdmin(id);
        return responder(buscar(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioAtualizacaoRequest request) {
        autenticado.exigirProprioOuAdmin(id);
        Usuario usuario = buscar(id);
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        repository.findByEmailIgnoreCase(email).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
            }
        });
        usuario.setNome(request.nome().strip());
        usuario.setEmail(email);
        return responder(repository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        autenticado.exigirProprioOuAdmin(id);
        Usuario usuario = buscar(id);
        try {
            repository.delete(usuario);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir um usuário vinculado a locais, relatos ou avisos", ex);
        }
    }

    private Usuario buscar(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado: " + id));
    }

    private UsuarioResponse responder(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil());
    }
}
