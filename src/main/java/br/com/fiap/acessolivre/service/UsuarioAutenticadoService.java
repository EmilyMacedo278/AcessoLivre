package br.com.fiap.acessolivre.service;

import br.com.fiap.acessolivre.entity.Local;
import br.com.fiap.acessolivre.entity.Usuario;
import br.com.fiap.acessolivre.enums.Perfil;
import br.com.fiap.acessolivre.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioAutenticadoService {
    private final UsuarioRepository usuarios;

    // O 'sub' do JWT agora é o ID imutável do usuário, e não seu e-mail.
    public Usuario atual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "É necessário fazer login");
        }
        Long usuarioId;
        try {
            usuarioId = Long.valueOf(auth.getName());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Token inválido: identificador do usuário incorreto");
        }
        return usuarios.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Conta autenticada não está cadastrada no AcessoLivre"));
    }

    public void exigirAdmin() {
        if (atual().getPerfil() != Perfil.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas administradores podem realizar esta ação");
        }
    }

    public void exigirProprioOuAdmin(Long usuarioId) {
        Usuario atual = atual();
        if (atual.getPerfil() != Perfil.ADMIN && !atual.getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não pode alterar esta conta");
        }
    }

    public void exigirResponsavelOuAdmin(Local local) {
        Usuario atual = atual();
        if (atual.getPerfil() == Perfil.ADMIN) return;
        if (atual.getPerfil() != Perfil.RESPONSAVEL || local.getResponsavel() == null
                || !atual.getId().equals(local.getResponsavel().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não é responsável por este local");
        }
    }
}
