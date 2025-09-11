package br.upf.ads175.critiquehub.repository;

import br.upf.ads175.critiquehub.entity.model.Favorito;
import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FavoritoRepository implements PanacheRepository<Favorito> {

    public List<Favorito> findByCategoria(CategoriaFavorito categoria) {
        return list("categoria", categoria);
    }
}
