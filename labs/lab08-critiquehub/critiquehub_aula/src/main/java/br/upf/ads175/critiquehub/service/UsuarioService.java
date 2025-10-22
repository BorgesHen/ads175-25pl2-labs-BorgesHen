package br.upf.ads175.critiquehub.service;

import br.upf.ads175.critiquehub.entity.enums.StatusUsuario;
import br.upf.ads175.critiquehub.entity.model.Usuario;
import br.upf.ads175.critiquehub.exception.DadosDuplicadosException;
import br.upf.ads175.critiquehub.exception.EntidadeNaoEncontradaException;
import br.upf.ads175.critiquehub.exception.RegraDeNegocioException;
import br.upf.ads175.critiquehub.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de negócio simplificado para operações relacionadas a usuários.
 */
@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    // ========================================================================
    // Operações de Criação e Atualização
    // ========================================================================

    @Transactional
    public Usuario criarUsuario(String email, String nomeUsuario, String nomeCompleto) {
        // Validações básicas
        validarDadosObrigatorios(email, nomeUsuario, nomeCompleto);
        verificarDuplicacao(email, nomeUsuario);

        // Criação da entidade
        Usuario novoUsuario = new Usuario(email.trim().toLowerCase(),
            nomeUsuario.trim(),
            nomeCompleto.trim());

        return usuarioRepository.salvar(novoUsuario);
    }

    @Transactional
    public Usuario atualizarPerfil(Long id, String nomeCompleto, String biografia) {
        Usuario usuario = buscarPorIdObrigatorio(id);

        if (nomeCompleto != null && !nomeCompleto.trim().isEmpty()) {
            usuario.setNomeCompleto(nomeCompleto.trim());
        }

        usuario.setBiografia(biografia != null ? biografia.trim() : null);
        return usuarioRepository.atualizar(usuario);
    }

    @Transactional
    public Usuario alterarStatus(Long id, StatusUsuario novoStatus) {
        Usuario usuario = buscarPorIdObrigatorio(id);

        if (usuario.getStatus() == StatusUsuario.SUSPENSO && novoStatus == StatusUsuario.INATIVO) {
            throw new RegraDeNegocioException("Usuário suspenso não pode ser marcado como inativo diretamente");
        }

        usuario.setStatus(novoStatus);
        return usuarioRepository.atualizar(usuario);
    }

    // ========================================================================
    // Operações de Consulta
    // ========================================================================

    public Usuario buscarPorIdObrigatorio(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }

        return usuarioRepository.buscarPorId(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com ID: " + id));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioRepository.buscarPorEmail(email.trim().toLowerCase());
    }

    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            return Optional.empty();
        }
        return usuarioRepository.buscarPorNomeUsuario(nomeUsuario.trim());
    }

    public List<Usuario> listarUsuariosAtivos() {
        return usuarioRepository.listarAtivos();
    }

    public List<Usuario> buscarPorNome(String fragmentoNome, boolean incluirInativos) {
        if (fragmentoNome == null || fragmentoNome.trim().length() < 2) {
            return List.of();
        }
        return usuarioRepository.buscarPorNome(fragmentoNome.trim(), !incluirInativos, 20);
    }

    // ========================================================================
    // Operações de Remoção
    // ========================================================================

    @Transactional
    public boolean inativarUsuario(Long id) {
        try {
            return alterarStatus(id, StatusUsuario.INATIVO) != null;
        } catch (EntidadeNaoEncontradaException e) {
            return false;
        }
    }

    @Transactional
    public boolean removerDefinitivamente(Long id) {
        return usuarioRepository.remover(id);
    }

    // ========================================================================
    // Métodos de Validação Privados
    // ========================================================================

    private void validarDadosObrigatorios(String email, String nomeUsuario, String nomeCompleto) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário é obrigatório");
        }

        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome completo é obrigatório");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email deve ter formato válido");
        }

        if (nomeUsuario.trim().length() < 3) {
            throw new IllegalArgumentException("Nome de usuário deve ter pelo menos 3 caracteres");
        }
    }

    private void verificarDuplicacao(String email, String nomeUsuario) {
        if (usuarioRepository.existePorEmail(email.trim().toLowerCase())) {
            throw new DadosDuplicadosException("Email já está em uso: " + email);
        }

        if (usuarioRepository.existePorNomeUsuario(nomeUsuario.trim())) {
            throw new DadosDuplicadosException("Nome de usuário já está em uso: " + nomeUsuario);
        }
    }

    // ========================================================================
    // Métodos Utilitários
    // ========================================================================

    public boolean podeExecutarAcoes(Usuario usuario) {
        return usuario != null && usuario.isAtivo();
    }

    public long contarUsuariosAtivos() {
        return usuarioRepository.listarAtivos().size();
    }
}
