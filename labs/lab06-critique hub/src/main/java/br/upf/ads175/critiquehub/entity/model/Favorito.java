package br.upf.ads175.critiquehub.entity.model;

import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private ItemCultural itemCultural;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CategoriaFavorito categoria;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public ItemCultural getItemCultural() { return itemCultural; }
    public void setItemCultural(ItemCultural itemCultural) { this.itemCultural = itemCultural; }

    public CategoriaFavorito getCategoria() { return categoria; }
    public void setCategoria(CategoriaFavorito categoria) { this.categoria = categoria; }
}
