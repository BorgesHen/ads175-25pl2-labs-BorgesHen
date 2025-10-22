package br.upf.ads175.critiquehub.entity.model;

import java.time.LocalDateTime;
import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

/**
 * Entidade que representa a tabela de junção entre Usuario e ItemCultural
 * com informações adicionais sobre o favorito
 */
@Entity
@Table(name = "lista_favoritos")
public class ListaFavoritos {

    @EmbeddedId
    private ListaFavoritosId id;

    // @MapsId indica que este relacionamento fornece o valor para parte da chave
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId") // Mapeia para o campo usuarioId da chave composta
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemCulturalId") // Mapeia para o campo itemCulturalId da chave composta
    @JoinColumn(name = "item_cultural_id")
    private ItemCultural itemCultural;

    @Column(name = "data_adicao", nullable = false)
    private LocalDateTime dataAdicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", length = 50, nullable = false)
    private CategoriaFavorito categoria;

    @Size(max = 500, message = "Observações não podem exceder 500 caracteres")
    @Column(name = "observacoes", length = 500)
    private String observacoes;

    // Construtor padrão obrigatório
    protected ListaFavoritos() {}

    // Construtor de conveniência
    public ListaFavoritos(Usuario usuario, ItemCultural itemCultural, CategoriaFavorito categoria) {
        this.usuario = usuario;
        this.itemCultural = itemCultural;
        this.categoria = categoria;
        this.dataAdicao = LocalDateTime.now();

        // Criar a chave composta automaticamente
        this.id = new ListaFavoritosId(usuario.getId(), itemCultural.getId());
    }

    // Construtor completo
    public ListaFavoritos(Usuario usuario, ItemCultural itemCultural,
                         CategoriaFavorito categoria, String observacoes) {
        this(usuario, itemCultural, categoria);
        this.observacoes = observacoes;
    }

    // Validações de ciclo de vida
    @PrePersist
    private void validarAntesPersistir() {
        if (dataAdicao == null) {
            dataAdicao = LocalDateTime.now();
        }
        if (categoria == null) {
            categoria = CategoriaFavorito.QUERO_ASSISTIR; // valor padrão
        }
    }

    // Getters e setters
    public ListaFavoritosId getId() { return id; }
    public void setId(ListaFavoritosId id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public ItemCultural getItemCultural() { return itemCultural; }
    public void setItemCultural(ItemCultural itemCultural) { this.itemCultural = itemCultural; }

    public LocalDateTime getDataAdicao() { return dataAdicao; }
    public void setDataAdicao(LocalDateTime dataAdicao) { this.dataAdicao = dataAdicao; }

    public CategoriaFavorito getCategoria() { return categoria; }
    public void setCategoria(CategoriaFavorito categoria) { this.categoria = categoria; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
