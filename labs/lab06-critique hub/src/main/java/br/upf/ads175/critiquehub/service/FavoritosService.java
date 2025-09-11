package br.upf.ads175.critiquehub.service;

import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;
import br.upf.ads175.critiquehub.entity.model.Favorito;
import br.upf.ads175.critiquehub.entity.model.ItemCultural;
import br.upf.ads175.critiquehub.entity.model.Usuario;
import br.upf.ads175.critiquehub.repository.FavoritoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FavoritosService {

    @Inject
    FavoritoRepository favoritoRepository;

    @Transactional
    public Favorito adicionarAosFavoritos(Usuario usuario, ItemCultural item, CategoriaFavorito categoria) {
        Favorito fav = new Favorito();
        fav.setUsuario(usuario);
        fav.setItemCultural(item);
        fav.setCategoria(categoria);
        favoritoRepository.persist(fav);
        return fav;
    }

    public List<Favorito> listarFavoritosPorCategoria(CategoriaFavorito categoria) {
        return favoritoRepository.findByCategoria(categoria);
    }

    @Transactional
    public void atualizarCategoria(Long id, CategoriaFavorito categoria) {
        Favorito f = favoritoRepository.findById(id);
        if (f != null) {
            f.setCategoria(categoria);
            favoritoRepository.persist(f);
        }
    }

    public List<Favorito> listarTodosFavoritos() {
        return favoritoRepository.listAll();
    }

    @Transactional
    public void removerDosFavoritos(Long id) {
        favoritoRepository.deleteById(id);
    }
}
