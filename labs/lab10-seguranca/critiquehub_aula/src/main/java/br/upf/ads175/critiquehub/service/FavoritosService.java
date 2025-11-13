package br.upf.ads175.critiquehub.service;

import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;import br.upf.ads175.critiquehub.entity.model.ItemCultural;import br.upf.ads175.critiquehub.entity.model.ListaFavoritos;import br.upf.ads175.critiquehub.entity.model.ListaFavoritosId;import br.upf.ads175.critiquehub.entity.model.Usuario;import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FavoritosService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Adiciona um item aos favoritos do usuário
     */
    @Transactional
    public void adicionarAosFavoritos(Long usuarioId, Long itemId,
                                     CategoriaFavorito categoria, String observacoes) {
        // Buscar entidades
        Usuario usuario = buscarUsuarioOuFalhar(usuarioId);
        ItemCultural item = buscarItemOuFalhar(itemId);

        // Verificar se já não é favorito
        if (jaEhFavorito(usuarioId, itemId)) {
            throw new IllegalStateException("Item já está na lista de favoritos");
        }

        // Criar e persistir o favorito
        ListaFavoritos favorito = new ListaFavoritos(usuario, item, categoria, observacoes);
        entityManager.persist(favorito);
    }

    /**
     * Remove um item dos favoritos
     */
    @Transactional
    public boolean removerDosFavoritos(Long usuarioId, Long itemId) {
        ListaFavoritosId id = new ListaFavoritosId(usuarioId, itemId);
        ListaFavoritos favorito = entityManager.find(ListaFavoritos.class, id);

        if (favorito != null) {
            entityManager.remove(favorito);
            return true;
        }
        return false;
    }

    /**
     * Lista favoritos de um usuário por categoria
     */
    public List<ListaFavoritos> listarFavoritosPorCategoria(Long usuarioId, CategoriaFavorito categoria) {
        return entityManager.createQuery(
            "SELECT lf FROM ListaFavoritos lf " +
            "JOIN FETCH lf.itemCultural " +
            "WHERE lf.usuario.id = :usuarioId " +
            "AND lf.categoria = :categoria " +
            "ORDER BY lf.dataAdicao DESC", ListaFavoritos.class)
            .setParameter("usuarioId", usuarioId)
            .setParameter("categoria", categoria)
            .getResultList();
    }

    /**
     * Lista todos os favoritos de um usuário
     */
    public List<ListaFavoritos> listarTodosFavoritos(Long usuarioId) {
        return entityManager.createQuery(
            "SELECT lf FROM ListaFavoritos lf " +
            "JOIN FETCH lf.itemCultural " +
            "WHERE lf.usuario.id = :usuarioId " +
            "ORDER BY lf.categoria, lf.dataAdicao DESC", ListaFavoritos.class)
            .setParameter("usuarioId", usuarioId)
            .getResultList();
    }

    /**
     * Atualiza categoria de um favorito
     */
    @Transactional
    public void atualizarCategoria(Long usuarioId, Long itemId, CategoriaFavorito novaCategoria) {
        ListaFavoritosId id = new ListaFavoritosId(usuarioId, itemId);
        ListaFavoritos favorito = entityManager.find(ListaFavoritos.class, id);

        if (favorito == null) {
            throw new IllegalArgumentException("Favorito não encontrado");
        }

        favorito.setCategoria(novaCategoria);
        // EntityManager detecta a mudança automaticamente
    }

    // Métodos auxiliares privados
    private Usuario buscarUsuarioOuFalhar(Long id) {
        Usuario usuario = entityManager.find(Usuario.class, id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado: " + id);
        }
        return usuario;
    }

    private ItemCultural buscarItemOuFalhar(Long id) {
        ItemCultural item = entityManager.find(ItemCultural.class, id);
        if (item == null) {
            throw new IllegalArgumentException("Item cultural não encontrado: " + id);
        }
        return item;
    }

    private boolean jaEhFavorito(Long usuarioId, Long itemId) {
        ListaFavoritosId id = new ListaFavoritosId(usuarioId, itemId);
        return entityManager.find(ListaFavoritos.class, id) != null;
    }
}
