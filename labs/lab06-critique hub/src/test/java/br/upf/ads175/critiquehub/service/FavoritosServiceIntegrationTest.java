package br.upf.ads175.critiquehub.service;

import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;
import br.upf.ads175.critiquehub.entity.model.Favorito;
import br.upf.ads175.critiquehub.entity.model.ItemCultural;
import br.upf.ads175.critiquehub.entity.model.Usuario;
import br.upf.ads175.critiquehub.repository.ItemCulturalRepository;
import br.upf.ads175.critiquehub.repository.UsuarioRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class FavoritosServiceIntegrationTest {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    ItemCulturalRepository itemRepository;

    @Inject
    FavoritosService favoritosService;

    @Test
    @Transactional
    public void testFluxoFavoritos() {
        Usuario usuario = new Usuario();
        usuario.setNome("Carlos");
        usuarioRepository.persist(usuario);

        ItemCultural item = new ItemCultural();
        item.setTitulo("Inception");
        itemRepository.persist(item);

        Favorito favorito = favoritosService.adicionarAosFavoritos(usuario, item, CategoriaFavorito.FILME);
        Assertions.assertNotNull(favorito.getId());

        List<Favorito> filmes = favoritosService.listarFavoritosPorCategoria(CategoriaFavorito.FILME);
        Assertions.assertEquals(1, filmes.size());

        favoritosService.atualizarCategoria(favorito.getId(), CategoriaFavorito.OUTRO);
        Favorito atualizado = favoritosService.listarTodosFavoritos().stream().filter(f -> f.getId().equals(favorito.getId())).findFirst().orElse(null);
        Assertions.assertNotNull(atualizado);
        Assertions.assertEquals(CategoriaFavorito.OUTRO, atualizado.getCategoria());

        List<Favorito> todos = favoritosService.listarTodosFavoritos();
        Assertions.assertEquals(1, todos.size());

        favoritosService.removerDosFavoritos(favorito.getId());
        List<Favorito> after = favoritosService.listarTodosFavoritos();
        Assertions.assertEquals(0, after.size());
    }
}
