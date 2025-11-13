package br.upf.ads175.critiquehub.entity.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Chave composta para a entidade ListaFavoritos
 * Representa a associação única entre um Usuario e um ItemCultural
 */
@Embeddable
public class ListaFavoritosId implements Serializable {

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "item_cultural_id")
    private Long itemCulturalId;

    // Construtor padrão obrigatório para JPA
    public ListaFavoritosId() {}

    // Construtor de conveniência
    public ListaFavoritosId(Long usuarioId, Long itemCulturalId) {
        this.usuarioId = usuarioId;
        this.itemCulturalId = itemCulturalId;
    }

    // equals() e hashCode() são OBRIGATÓRIOS para chaves compostas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListaFavoritosId that = (ListaFavoritosId) o;
        return Objects.equals(usuarioId, that.usuarioId) &&
               Objects.equals(itemCulturalId, that.itemCulturalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, itemCulturalId);
    }

    @Override
    public String toString() {
        return String.format("ListaFavoritosId{usuarioId=%d, itemCulturalId=%d}",
                           usuarioId, itemCulturalId);
    }

    // Getters e setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getItemCulturalId() { return itemCulturalId; }
    public void setItemCulturalId(Long itemCulturalId) { this.itemCulturalId = itemCulturalId; }
}
