package br.upf.ads175.critiquehub.repository;

import br.upf.ads175.critiquehub.entity.enums.StatusUsuario;
import br.upf.ads175.critiquehub.entity.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de acesso aos dados da entidade Usuario.
 *
 * Implementa o padrão Repository fornecendo uma abstração limpa entre
 * a camada de serviço e a camada de persistência. Centraliza todas as
 * operações de consulta e persistência relacionadas aos usuários.
 *
 * Características principais:
 * - Encapsulamento completo do EntityManager
 * - Queries otimizadas e nomeadas
 * - Tratamento adequado de exceções
 * - API semântica e expressiva
 */
@ApplicationScoped // <1>
public class UsuarioRepository {

    @Inject // <2>
    EntityManager entityManager;

    // ========================================================================
    // Operações CRUD Básicas
    // ========================================================================

    /**
     * Persiste um novo usuário no banco de dados.
     *
     * @param usuario usuário a ser persistido (deve ser novo, sem ID)
     * @return o usuário persistido com ID gerado
     * @throws IllegalArgumentException se usuário já possui ID
     */
    public Usuario salvar(Usuario usuario) { // <3>
        if (usuario.getId() != null) {
            throw new IllegalArgumentException("Usuário já possui ID. Use atualizar() para modificar usuário existente.");
        }

        entityManager.persist(usuario); // <4>
        return usuario;
    }

    /**
     * Atualiza um usuário existente no banco de dados.
     *
     * @param usuario usuário a ser atualizado (deve ter ID)
     * @return o usuário atualizado e gerenciado
     * @throws IllegalArgumentException se usuário não possui ID
     */
    public Usuario atualizar(Usuario usuario) {
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("Usuário deve ter ID para ser atualizado. Use salvar() para novos usuários.");
        }

        return entityManager.merge(usuario); // <5>
    }

    /**
     * Busca usuário por ID.
     *
     * @param id identificador do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<Usuario> buscarPorId(Long id) { // <6>
        Usuario usuario = entityManager.find(Usuario.class, id);
        return Optional.ofNullable(usuario);
    }

    /**
     * Remove usuário do banco de dados.
     *
     * @param id identificador do usuário a ser removido
     * @return true se usuário foi removido, false se não foi encontrado
     */
    public boolean remover(Long id) { // <7>
        return buscarPorId(id)
                .map(usuario -> {
                    entityManager.remove(usuario);
                    return true;
                })
                .orElse(false);
    }

    // ========================================================================
    // Consultas por Campos Únicos
    // ========================================================================

    /**
     * Busca usuário por email (campo único).
     *
     * @param email email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<Usuario> buscarPorEmail(String email) { // <8>
        try {
            Usuario usuario = entityManager.createNamedQuery("Usuario.buscarPorEmail", Usuario.class)
                                          .setParameter("email", email.toLowerCase().trim())
                                          .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) { // <9>
            return Optional.empty();
        }
    }

    /**
     * Busca usuário por nome de usuário (campo único).
     *
     * @param nomeUsuario nome de usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<Usuario> buscarPorNomeUsuario(String nomeUsuario) {
        try {
            Usuario usuario = entityManager.createNamedQuery("Usuario.buscarPorNomeUsuario", Usuario.class)
                                          .setParameter("nomeUsuario", nomeUsuario.trim())
                                          .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Verifica se já existe usuário com o email especificado.
     *
     * @param email email a verificar
     * @return true se email já está em uso
     */
    public boolean existePorEmail(String email) { // <10>
        Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)", Long.class)
                .setParameter("email", email.trim())
                .getSingleResult();
        return count > 0;
    }

    /**
     * Verifica se já existe usuário com o nome de usuário especificado.
     *
     * @param nomeUsuario nome de usuário a verificar
     * @return true se nome de usuário já está em uso
     */
    public boolean existePorNomeUsuario(String nomeUsuario) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.nomeUsuario = :nomeUsuario", Long.class)
                .setParameter("nomeUsuario", nomeUsuario.trim())
                .getSingleResult();
        return count > 0;
    }

    // ========================================================================
    // Consultas por Status e Filtros
    // ========================================================================

    /**
     * Lista todos os usuários ativos, ordenados por data de registro (mais recentes primeiro).
     *
     * @return lista de usuários ativos
     */
    public List<Usuario> listarAtivos() { // <11>
        return entityManager.createNamedQuery("Usuario.listarAtivos", Usuario.class)
                           .getResultList();
    }

    /**
     * Lista usuários ativos com paginação.
     *
     * @param pagina número da página (base 0)
     * @param tamanhoPagina quantidade de registros por página
     * @return lista paginada de usuários ativos
     */
    public List<Usuario> listarAtivos(int pagina, int tamanhoPagina) { // <12>
        return entityManager.createNamedQuery("Usuario.listarAtivos", Usuario.class)
                           .setFirstResult(pagina * tamanhoPagina)
                           .setMaxResults(tamanhoPagina)
                           .getResultList();
    }

    /**
     * Busca usuários registrados em um período específico.
     *
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return lista de usuários registrados no período
     */
    public List<Usuario> buscarPorPeriodoRegistro(LocalDate dataInicio, LocalDate dataFim) { // <13>
        return entityManager.createQuery(
                "SELECT u FROM Usuario u WHERE u.dataRegistro BETWEEN :dataInicio AND :dataFim ORDER BY u.dataRegistro DESC",
                Usuario.class)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getResultList();
    }

    /**
     * Busca usuários por fragmento do nome (busca flexível).
     *
     * @param fragmentoNome parte do nome a buscar
     * @param apenasAtivos se true, busca apenas usuários ativos
     * @param limite quantidade máxima de resultados
     * @return lista de usuários que correspondem à busca
     */
    public List<Usuario> buscarPorNome(String fragmentoNome, boolean apenasAtivos, int limite) { // <14>
        String jpql = """
                SELECT u FROM Usuario u
                WHERE LOWER(u.nomeCompleto) LIKE LOWER(:fragmento)
                   OR LOWER(u.nomeUsuario) LIKE LOWER(:fragmento)
                """;

        if (apenasAtivos) {
            jpql += " AND u.status = :status";
        }

        jpql += " ORDER BY u.nomeCompleto";

        var query = entityManager.createQuery(jpql, Usuario.class)
                .setParameter("fragmento", "%" + fragmentoNome.trim() + "%")
                .setMaxResults(limite);

        if (apenasAtivos) {
            query.setParameter("status", StatusUsuario.ATIVO);
        }

        return query.getResultList();
    }

    // ========================================================================
    // Operações Utilitárias
    // ========================================================================

    /**
     * Recarrega usuário do banco de dados, descartando mudanças não persistidas.
     *
     * @param usuario usuário a ser recarregado
     */
    public void recarregar(Usuario usuario) { // <15>
        if (entityManager.contains(usuario)) {
            entityManager.refresh(usuario);
        }
    }

    /**
     * Força sincronização das mudanças pendentes com o banco de dados.
     */
    public void flush() { // <16>
        entityManager.flush();
    }
}
